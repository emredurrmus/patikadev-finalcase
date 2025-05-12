package com.edurmus.librarymanagement.service;

import com.edurmus.librarymanagement.exception.book.BookAlreadyReturnedException;
import com.edurmus.librarymanagement.exception.book.BookNotAvailableException;
import com.edurmus.librarymanagement.exception.book.BookNotFoundException;
import com.edurmus.librarymanagement.exception.borrow.BorrowingNotFoundException;
import com.edurmus.librarymanagement.model.dto.response.BorrowingDTO;
import com.edurmus.librarymanagement.model.dto.response.BorrowingSuccessResponse;
import com.edurmus.librarymanagement.model.dto.response.ReturnBookResponse;
import com.edurmus.librarymanagement.model.entity.Book;
import com.edurmus.librarymanagement.model.entity.Borrowing;
import com.edurmus.librarymanagement.model.entity.Role;
import com.edurmus.librarymanagement.model.entity.User;
import com.edurmus.librarymanagement.model.enums.BorrowingStatus;
import com.edurmus.librarymanagement.model.enums.UserRole;
import com.edurmus.librarymanagement.repository.BookRepository;
import com.edurmus.librarymanagement.repository.BorrowingRepository;
import com.edurmus.librarymanagement.repository.UserRepository;
import com.edurmus.librarymanagement.service.impl.BorrowingServiceImpl;
import com.edurmus.librarymanagement.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.security.SecurityUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class BorrowingServiceImplTest {

    @Mock private BookRepository bookRepository;
    @Mock private BorrowingRepository borrowingRepository;
    @Mock private UserRepository userRepository;

    @Spy
    @InjectMocks
    private BorrowingServiceImpl borrowingService;

    @BeforeEach
    void setUp() {
        borrowingService = new BorrowingServiceImpl(bookRepository, borrowingRepository, userRepository);

        User user = new User();
        user.setUsername("emre");
        user.setPassword("1234");
        Role userRole = new Role();
        userRole.setUserRole(UserRole.ROLE_PATRON);
        user.setRoles(Set.of(userRole));

        lenient().when(userRepository.findByUsername("emre")).thenReturn(Optional.of(user));

        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getName()).thenReturn("emre");

        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void shouldBorrowBookSuccessfully() {
        User user = new User();
        user.setUsername("emre");

        Book book = new Book();
        book.setId(1L);
        book.setAvailable(true);

        when(userRepository.findByUsername("emre")).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any())).thenReturn(book);
        when(borrowingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));


        BorrowingSuccessResponse result = borrowingService.borrowBook(1L);

        assertNotNull(result);
        log.info("shouldBorrowBookSuccessfully passed.");
    }

    @Test
    void shouldThrowBookNotFoundException_whenBookNotFound() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> borrowingService.borrowBook(1L));
    }

    @Test
    void shouldThrowBookNotAvailableException_whenBookUnavailable() {
        Book book = new Book();
        book.setAvailable(false);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        assertThrows(BookNotAvailableException.class, () -> borrowingService.borrowBook(1L));
    }

    @Test
    void shouldThrowException_whenBorrowingNotBelongsToCurrentUser() {
        Borrowing borrowing = new Borrowing();
        User actualUser = new User();
        actualUser.setUsername("ahmet"); // different user

        borrowing.setUser(actualUser);
        borrowing.setDueDate(LocalDateTime.now().minusDays(5));
        borrowing.setId(1L);


        User loggedInUser = new User();
        loggedInUser.setUsername("emre");

        when(borrowingRepository.findById(1L)).thenReturn(Optional.of(borrowing));
        try (MockedStatic<SecurityUtil> mockedSecurity = mockStatic(SecurityUtil.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUserName).thenReturn("emre");

            BorrowingNotFoundException exception = assertThrows(BorrowingNotFoundException.class,
                    () -> borrowingService.returnBook(1L));

            assertEquals("This user does not have a borrowing record with id: 1", exception.getMessage());
            log.info("shouldThrowException_whenBorrowingNotBelongsToCurrentUser passed.");
        }
    }

    @Test
    void shouldSetStatusReturned_whenBorrowingIsNotOverdue() {
        Borrowing borrowing = new Borrowing();
        Book book = new Book();
        book.setAvailable(false);

        User user = new User();
        user.setUsername("testuser");
        borrowing.setUser(user);
        borrowing.setBook(book);

        borrowing.setDueDate(LocalDateTime.now().plusDays(1));
        borrowing.setStatus(BorrowingStatus.BORROWED);
        borrowing.setId(1L);

        when(borrowingRepository.findById(1L)).thenReturn(Optional.of(borrowing));
        when(borrowingRepository.save(any(Borrowing.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUserName).thenReturn("testuser");

            ReturnBookResponse response = borrowingService.returnBook(1L);

            assertEquals(BorrowingStatus.RETURNED, response.borrowingDTO().getStatus());
            assertFalse(response.isOverdue());
            assertEquals(BigDecimal.ZERO, response.borrowingDTO().getFine());

            verify(borrowingRepository).save(any(Borrowing.class));
            verify(bookRepository).save(any(Book.class));
        }
    }


    @Test
    void shouldReturnBookAsOverdue_whenOverdue() {
        Borrowing borrowing = new Borrowing();
        Book book = new Book();

        User user = new User();
        user.setId(1L);
        user.setUsername("emre");
        user.setOverdueFine(BigDecimal.ZERO);

        borrowing.setBook(book);
        borrowing.setUser(user);
        borrowing.setDueDate(LocalDateTime.now().minusDays(5));

        when(borrowingRepository.findById(1L)).thenReturn(Optional.of(borrowing));
        when(bookRepository.save(any())).thenReturn(book);
        when(borrowingRepository.save(any())).thenReturn(borrowing);
        when(borrowingRepository.countByUserIdAndReturnDateAfterDueDate(1L)).thenReturn(1L);
        when(userRepository.save(any())).thenReturn(user);


        ReturnBookResponse response = borrowingService.returnBook(1L);

        assertEquals(BorrowingStatus.OVERDUE, borrowing.getStatus());
        assertTrue(response.isOverdue());
        log.info("shouldReturnBookAsOverdue_whenOverdue passed.");
    }

    @Test
    void shouldThrowBorrowingNotFoundException_whenBorrowingNotFound() {
        when(borrowingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BorrowingNotFoundException.class, () -> borrowingService.returnBook(1L));
    }

    @Test
    void shouldReturnUserBorrowingHistory() {
        User user = new User();
        user.setUsername("emre");

        Borrowing borrowing = new Borrowing();
        when(borrowingRepository.findByUser(user)).thenReturn(List.of(borrowing));
        when(userRepository.findByUsername("emre")).thenReturn(Optional.of(user));

        List<BorrowingDTO> history = borrowingService.getUserBorrowingHistory();

        assertFalse(history.isEmpty());
        assertEquals(1, history.size());

        log.info("shouldReturnUserBorrowingHistory passed.");
    }

    @Test
    void shouldThrowBookAlreadyReturnedException_whenBookAlreadyReturned() {
        Borrowing borrowing = new Borrowing();
        borrowing.setId(1L);
        borrowing.setReturnDate(LocalDateTime.now());

        when(borrowingRepository.findById(anyLong())).thenReturn(Optional.of(borrowing));

        assertThrows(BookAlreadyReturnedException.class, () -> borrowingService.returnBook(1L));
    }

    @Test
    void shouldDisableUser_whenOverdueCountIsTwoOrMore() {
        Borrowing borrowing = new Borrowing();
        Book book = new Book();

        User user = new User();
        user.setId(1L);
        user.setUsername("emre");
        user.setOverdueFine(BigDecimal.ZERO);
        user.setEnabled(true);

        borrowing.setBook(book);
        borrowing.setUser(user);
        borrowing.setDueDate(LocalDateTime.now().minusDays(5));

        when(borrowingRepository.findById(1L)).thenReturn(Optional.of(borrowing));
        when(bookRepository.save(any())).thenReturn(book);
        when(borrowingRepository.save(any())).thenReturn(borrowing);
        when(borrowingRepository.countByUserIdAndReturnDateAfterDueDate(1L)).thenReturn(2L);
        when(userRepository.save(any())).thenReturn(user);

        ReturnBookResponse response = borrowingService.returnBook(1L);

        assertFalse(user.isEnabled());
        assertEquals(BorrowingStatus.OVERDUE, borrowing.getStatus());
        assertTrue(response.isOverdue());
    }




    @Test
    void shouldReturnAllBorrowingHistory() {
        when(borrowingRepository.findAll()).thenReturn(List.of(new Borrowing()));

        List<BorrowingDTO> history = borrowingService.getAllBorrowingHistory();

        assertFalse(history.isEmpty());
        log.info("shouldReturnAllBorrowingHistory passed.");
    }

    @Test
    void shouldGenerateOverdueReportSuccessfully() {
        Borrowing borrowing = new Borrowing();

        User user = new User();
        user.setFirstName("Emre");
        user.setLastName("Durmus");
        user.setEmail("emredurmus@example.com");
        borrowing.setUser(user);

        when(borrowingRepository.findByReturnDateAfterAndDueDate())
                .thenReturn(List.of(borrowing));

        String report = borrowingService.generateOverdueReport();

        assertTrue(report.contains("OVERDUE BOOK REPORT"));
        assertTrue(report.contains("emredurmus@example.com"));
        log.info("shouldGenerateOverdueReportSuccessfully passed.");
    }
}
