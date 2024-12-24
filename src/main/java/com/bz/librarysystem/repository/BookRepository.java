package com.bz.librarysystem.repository;

import com.bz.librarysystem.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    List<Book> findByIsbn(String isbn);
    List<Book> findByTitleAndAuthor(String title, String author);
}


