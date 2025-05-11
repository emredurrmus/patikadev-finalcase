package com.edurmus.librarymanagement.repository;

import com.edurmus.librarymanagement.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    List<Book> findAllByActiveIsTrue();

    List<Book> findAllByIsAvailableIsTrueAndActiveIsTrue();

}
