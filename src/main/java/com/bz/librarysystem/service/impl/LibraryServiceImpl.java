package com.bz.librarysystem.service.impl;

import com.bz.librarysystem.dto.BookDto;
import com.bz.librarysystem.dto.BorrowerDto;
import com.bz.librarysystem.entity.Book;
import com.bz.librarysystem.entity.Borrower;
import com.bz.librarysystem.entity.LoanRecord;
import com.bz.librarysystem.exception.BookAlreadyBorrowedException;
import com.bz.librarysystem.exception.DuplicateBorrowerEmailException;
import com.bz.librarysystem.exception.ResourceNotFoundException;
import com.bz.librarysystem.repository.BookRepository;
import com.bz.librarysystem.repository.BorrowerRepository;
import com.bz.librarysystem.repository.LoanRecordRepository;
import com.bz.librarysystem.service.LibraryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class LibraryServiceImpl implements LibraryService {
    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private final LoanRecordRepository loanRepository;

    private final ModelMapper modelMapper;


    private final ConcurrentHashMap<UUID, ReentrantLock> bookLocks = new ConcurrentHashMap<>();

    public LibraryServiceImpl(BookRepository bookRepository,
                              BorrowerRepository borrowerRepository,
                              LoanRecordRepository loanRepository,
                              ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
        this.loanRepository = loanRepository;
        this.modelMapper = modelMapper;
    }


    private ReentrantLock getOrCreateLock(UUID bookId) {
        return bookLocks.computeIfAbsent(bookId, k -> new ReentrantLock());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookDto registerBook(BookDto bookDto) {
        System.out.println("bookDto is " + bookDto.getId() + ":" + bookDto.getTitle());

        Book book = new Book();
        BeanUtils.copyProperties(bookDto, book);
        System.out.println("book is " + book.getTitle());
        Book savedBook =  bookRepository.save(book);
        BeanUtils.copyProperties(savedBook, bookDto);
        return bookDto;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public BorrowerDto registerBorrower(BorrowerDto borrowerDto) {
        if (borrowerDto == null) {
            throw new IllegalArgumentException("BorrowerDto cannot be null");
        }
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerDto, borrower);

        if (borrowerRepository.findByEmail(borrower.getEmail()).isPresent()) {
            throw new DuplicateBorrowerEmailException("Borrower with this email already exists");
        }
        try {
            Borrower savedBorrower = borrowerRepository.save(borrower);
            BeanUtils.copyProperties(savedBorrower, borrowerDto);
            return borrowerDto;

        } catch (DataIntegrityViolationException e) {
            throw new DuplicateBorrowerEmailException("Borrower with this email already exists");
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Retryable(maxAttempts = 3, value = ObjectOptimisticLockingFailureException.class)
    public LoanRecord borrowBook(UUID bookId, UUID borrowerId) {
        ReentrantLock lock = getOrCreateLock(bookId);
        lock.lock();
        try {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

            if (book.isBorrowed()) {
                throw new BookAlreadyBorrowedException("Book is borrowed already");
            }

            Borrower borrower = borrowerRepository.findById(borrowerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Borrower not found"));

            book.setBorrowed(true);
            bookRepository.save(book);

            LoanRecord loan = new LoanRecord();
            loan.setBook(book);
            loan.setBorrower(borrower);
            loan.setBorrowedAt(LocalDateTime.now());

            return loanRepository.save(loan);
        } finally {
            lock.unlock();
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Retryable(maxAttempts = 3, value = ObjectOptimisticLockingFailureException.class)
    public LoanRecord returnBook(UUID bookId) {
        ReentrantLock lock = getOrCreateLock(bookId);
        lock.lock();
        try {
            LoanRecord loan = loanRepository.findByBookIdAndReturnedAtIsNull(bookId)
                    .orElseThrow(() -> new ResourceNotFoundException("No active loan found for this book"));

            Book book = loan.getBook();
            book.setBorrowed(false);
            bookRepository.save(book);

            loan.setReturnedAt(LocalDateTime.now());
            return loanRepository.save(loan);
        } finally {
            lock.unlock();
        }
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(book -> modelMapper.map(book, BookDto.class))
                .toList();
    }
}
