package com.bz.librarysystem.service;

import com.bz.librarysystem.dto.BookDto;
import com.bz.librarysystem.dto.BorrowerDto;
import com.bz.librarysystem.entity.Book;
import com.bz.librarysystem.entity.Borrower;
import com.bz.librarysystem.entity.LoanRecord;

import java.util.List;
import java.util.UUID;

public interface LibraryService {

    public BookDto registerBook(BookDto book);
    public BorrowerDto registerBorrower(BorrowerDto borrower);
    public LoanRecord borrowBook(UUID bookId, UUID borrowerId);
    public LoanRecord returnBook(UUID bookId);
    public List<BookDto> getAllBooks();
}
