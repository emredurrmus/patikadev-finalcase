package com.edurmus.librarymanagement.repository;

import com.edurmus.librarymanagement.model.entity.Borrowing;
import com.edurmus.librarymanagement.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {

    @Query("SELECT COUNT(b) FROM Borrowing b WHERE b.user.id = :userId AND b.returnDate > b.dueDate")
    long countByUserIdAndReturnDateAfterDueDate(Long userId);

    List<Borrowing> findByReturnDateIsNullAndDueDateBefore(LocalDate date);

    List<Borrowing> findByUser(User user);
}
