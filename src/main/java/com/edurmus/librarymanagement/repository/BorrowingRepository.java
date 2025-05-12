package com.edurmus.librarymanagement.repository;

import com.edurmus.librarymanagement.model.entity.Borrowing;
import com.edurmus.librarymanagement.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {

    @Query("SELECT COUNT(b) FROM Borrowing b WHERE b.returnDate IS NOT NULL AND b.user.id = :userId AND b.returnDate > b.dueDate")
    long countByUserIdAndReturnDateAfterDueDate(Long userId);

    @Query("SELECT b FROM Borrowing b WHERE b.returnDate IS NOT NULL AND b.returnDate > b.dueDate")
    List<Borrowing> findByReturnDateAfterAndDueDate();

    List<Borrowing> findByUser(User user);
}
