package com.edurmus.librarymanagement.service.impl;

import com.edurmus.librarymanagement.exception.book.BookAlreadyReturnedException;
import com.edurmus.librarymanagement.exception.book.BookNotAvailableException;
import com.edurmus.librarymanagement.exception.book.BookNotFoundException;
import com.edurmus.librarymanagement.exception.borrow.BorrowingNotFoundException;
import com.edurmus.librarymanagement.model.dto.response.BorrowingDTO;
import com.edurmus.librarymanagement.model.dto.response.BorrowingSuccessResponse;
import com.edurmus.librarymanagement.model.dto.response.ReturnBookResponse;
import com.edurmus.librarymanagement.model.entity.Book;
import com.edurmus.librarymanagement.model.entity.Borrowing;
import com.edurmus.librarymanagement.model.entity.User;
import com.edurmus.librarymanagement.model.enums.BorrowingStatus;
import com.edurmus.librarymanagement.model.mapper.BorrowingMapper;
import com.edurmus.librarymanagement.repository.BookRepository;
import com.edurmus.librarymanagement.repository.BorrowingRepository;
import com.edurmus.librarymanagement.repository.UserRepository;
import com.edurmus.librarymanagement.service.BorrowingService;
import com.edurmus.librarymanagement.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BorrowingServiceImpl implements BorrowingService {

    private final BookRepository bookRepository;
    private final BorrowingRepository borrowingRepository;
    private final UserRepository userRepository;

    @Autowired
    public BorrowingServiceImpl(BookRepository bookRepository, BorrowingRepository borrowingRepository,
                                UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.borrowingRepository = borrowingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BorrowingSuccessResponse borrowBook(Long bookId) {
        User user = getCurrentUser();
        log.info("User '{}' attempting to borrow book {}",  user.getUsername(), bookId);
        Book book = getAvailableBookOrThrow(bookId);

        book.setAvailable(false);
        bookRepository.save(book);

        Borrowing borrowing = createBorrowing(user, book);
        Borrowing savedBorrowing = borrowingRepository.save(borrowing);

        return BorrowingMapper.INSTANCE.toSuccessResponse(savedBorrowing);
    }

    private Book getAvailableBookOrThrow(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));

        if (!book.isAvailable()) {
            throw new BookNotAvailableException("The book is not available for borrowing");
        }
        return book;
    }


    private Borrowing createBorrowing(User user, Book book) {
        return Borrowing.builder()
                .user(user)
                .book(book)
                .borrowingDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusWeeks(2))
                .status(BorrowingStatus.BORROWED)
                .fine(BigDecimal.ZERO)
                .build();
    }



    @Override
    @Transactional
    public ReturnBookResponse returnBook(Long borrowingId) {
        Optional<Borrowing> borrowingOptional = borrowingRepository.findById(borrowingId);
        Borrowing borrowing = borrowingOptional.orElseThrow(() -> new BorrowingNotFoundException("Borrowing record not found with id: " + borrowingId));

        isAlreadyReturned(borrowing);
        // If user does not have a borrowing record with this id, throw an exception
        checkUserHasBorrowing(borrowing);

        borrowing.setReturnDate(LocalDateTime.now());
        boolean isOverDue = borrowing.isOverdue();
        BigDecimal fine = BigDecimal.ZERO;

        if (isOverDue) {
            fine = handleOverdueAndGetFine(borrowing);
            borrowing.setStatus(BorrowingStatus.OVERDUE);
        } else {
            borrowing.setStatus(BorrowingStatus.RETURNED);
        }
        borrowing.setFine(fine);
        Borrowing savedBorrowing = borrowingRepository.save(borrowing);
        log.info("User '{}' is returning book with borrowing ID: {}", borrowing.getUser().getUsername(), borrowingId);
        BorrowingDTO borrowingDTO = BorrowingMapper.INSTANCE.toDto(savedBorrowing);
        updateBookAvailability(borrowing.getBook());
        return new ReturnBookResponse(borrowingDTO, isOverDue);
    }

    private void isAlreadyReturned(Borrowing borrowing) {
        if (borrowing.getReturnDate() != null) {
            throw new BookAlreadyReturnedException("The book has already been returned");
        }
    }

    private void updateBookAvailability(Book book) {
        book.setAvailable(true);
        bookRepository.save(book);
    }

    private BigDecimal handleOverdueAndGetFine(Borrowing borrowing) {
        User user = borrowing.getUser();
        BigDecimal fine = borrowing.calculateOverdueFine();
        user.setOverdueFine(user.getOverdueFine().add(fine));
        log.warn("User '{}' has overdue book. Fine applied: {}", user.getUsername(), fine);

        long overdueCount = borrowingRepository.countByUserIdAndReturnDateAfterDueDate(user.getId());
        if (overdueCount >= 2) {
            user.setEnabled(false);
        }

        userRepository.save(user);
        log.info("User '{}' has been disabled due to multiple overdue books.", user.getUsername());
        return fine;
    }

    private void checkUserHasBorrowing(Borrowing borrowing) {
        String username = getCurrentUser().getUsername();
        if (!borrowing.getUser().getUsername().equals(username)) {
            throw new BorrowingNotFoundException("This user does not have a borrowing record with id: " + borrowing.getId());
        }
    }

    @Override
    public List<BorrowingDTO> getUserBorrowingHistory() {
        List<Borrowing> borrowings = borrowingRepository.findByUser(getCurrentUser());
        return borrowings.stream().map(BorrowingMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public List<BorrowingDTO> getAllBorrowingHistory() {
        List<Borrowing> borrowings = borrowingRepository.findAll();
        return borrowings.stream().map(BorrowingMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public String generateOverdueReport() {
        List<Borrowing> overdueList = findOverdueBorrowings();

        int totalOverdue = overdueList.size();
        Map<User, List<Borrowing>> userOverdueMap = groupBorrowingsByUser(overdueList);

        StringBuilder reportBuilder = buildReportHeader(totalOverdue, userOverdueMap.size());
        appendUserOverdueDetails(userOverdueMap, reportBuilder);
        appendReportFooter(reportBuilder);

        log.info("Generating overdue book report...");
        return reportBuilder.toString();
    }

    private StringBuilder buildReportHeader(int totalOverdue, int totalUsersWithOverdues) {
        return new StringBuilder()
                .append("""
                    OVERDUE BOOK REPORT
                    ----------------------
                    Total Overdue Books: %d
                    Total Users with Overdues: %d

                    """.formatted(totalOverdue, totalUsersWithOverdues));
    }

    private void appendUserOverdueDetails(Map<User, List<Borrowing>> userOverdueMap, StringBuilder reportBuilder) {
        for (Map.Entry<User, List<Borrowing>> entry : userOverdueMap.entrySet()) {
            User user = entry.getKey();
            List<Borrowing> borrowings = entry.getValue();
            int count = borrowings.size();
            BigDecimal totalFine = borrowings.stream()
                    .map(Borrowing::getFine)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            reportBuilder.append(" - %s (%s): %d book(s) overdue | Total fine: %.2f\n".formatted(
                    user.getFirstName() + " " + user.getLastName(),
                    user.getEmail(),
                    count,
                    totalFine
            ));
        }
    }

    private void appendReportFooter(StringBuilder reportBuilder) {
        reportBuilder.append("\nGenerated at: %s".formatted(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    private Map<User, List<Borrowing>> groupBorrowingsByUser(List<Borrowing> overdueList) {
        return overdueList.stream()
                .collect(Collectors.groupingBy(Borrowing::getUser));
    }


    private List<Borrowing> findOverdueBorrowings() {
        return borrowingRepository.findByReturnDateAfterAndDueDate();
    }

    public User getCurrentUser() {
        String username = SecurityUtils.getCurrentUserName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with this username: " + username));
    }

}
