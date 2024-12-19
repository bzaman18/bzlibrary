package com.bz.librarysystem.service;

import com.bz.librarysystem.dto.BookDto;
import com.bz.librarysystem.dto.BorrowerDto;
import com.bz.librarysystem.entity.Book;
import com.bz.librarysystem.entity.Borrower;
import com.bz.librarysystem.entity.LoanRecord;
import com.bz.librarysystem.exception.BookAlreadyBorrowedException;
import com.bz.librarysystem.exception.ResourceNotFoundException;
import com.bz.librarysystem.repository.BookRepository;
import com.bz.librarysystem.repository.BorrowerRepository;
import com.bz.librarysystem.repository.LoanRecordRepository;
import com.bz.librarysystem.service.impl.LibraryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private LoanRecordRepository loanRecordRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private LibraryServiceImpl libraryService;

    private Book testBook;
    private Borrower testBorrower;
    private BookDto bookDTO;
    private BorrowerDto borrowerDTO;

    private LoanRecord loanRecord;


    private static final UUID TEST_BOOK_UUID = UUID.randomUUID();
    private static final UUID TEST_BORROWER_UUID = UUID.randomUUID();
    private static final UUID TEST_LOANRECORD_UUID = UUID.randomUUID();


    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(TEST_BOOK_UUID);
        testBook.setIsbn("978-0-7475-3269-9");
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setBorrowed(false);

        testBorrower = new Borrower();
        testBorrower.setId(TEST_BORROWER_UUID);
        testBorrower.setName("Test Borrower");
        testBorrower.setEmail("test@example.com");

        bookDTO = new BookDto();
        bookDTO.setIsbn("978-0-7475-3269-9");
        bookDTO.setTitle("Test Book");
        bookDTO.setAuthor("Test Author");
        bookDTO.setVersion(1L);

        borrowerDTO = new BorrowerDto();
        borrowerDTO.setName("Test Borrower");
        borrowerDTO.setEmail("test@example.com");

        loanRecord = new LoanRecord();
        loanRecord.setBook(testBook);
        loanRecord.setBorrower(testBorrower);
        loanRecord.setBorrowedAt(LocalDateTime.now().minusDays(20));
        loanRecord.setId(TEST_LOANRECORD_UUID);
        loanRecord.setReturnedAt(null);
    }

    @Test
    void registerBook_Success() {
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        // When
        BookDto result = libraryService.registerBook(bookDTO);

        // Then
        assertNotNull(result);
        assertEquals(bookDTO.getIsbn(), result.getIsbn());
        assertEquals(bookDTO.getTitle(), result.getTitle());
        assertEquals(bookDTO.getAuthor(), result.getAuthor());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void registerBorrower_Success() {
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(testBorrower);

        BorrowerDto result = libraryService.registerBorrower(borrowerDTO);

        assertNotNull(result);
        assertEquals(testBorrower.getName(), result.getName());
        assertEquals(testBorrower.getEmail(), result.getEmail());
        verify(borrowerRepository).save(any(Borrower.class));
    }

    @Test
    void getAllBooks_Success() {

        List<Book> books = List.of(testBook);
        when(bookRepository.findAll()).thenReturn(books);
        when(modelMapper.map(testBook, BookDto.class)).thenReturn(bookDTO);

        // When
        List<BookDto> result = libraryService.getAllBooks();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBook.getIsbn(), result.get(0).getIsbn());
        assertEquals(testBook.getTitle(), result.get(0).getTitle());
        assertEquals(testBook.getAuthor(), result.get(0).getAuthor());

        verify(bookRepository).findAll();
        verify(modelMapper).map(testBook, BookDto.class);

    }

    @Test
    void borrowBook_BookNotFound() {
        when(bookRepository.findById(TEST_BOOK_UUID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            libraryService.borrowBook(TEST_BOOK_UUID, TEST_BORROWER_UUID)
        );
        verify(bookRepository).findById(TEST_BOOK_UUID);
    }

    @Test
    void borrowBook_AlreadyBorrowed() {
        testBook.setBorrowed(true);
        when(bookRepository.findById(TEST_BOOK_UUID)).thenReturn(Optional.of(testBook));

        assertThrows(BookAlreadyBorrowedException.class, () ->
            libraryService.borrowBook(TEST_BOOK_UUID, TEST_BORROWER_UUID)
        );
        verify(bookRepository).findById(TEST_BOOK_UUID);
    }

    @Test
    void returnBook_Success() {

        testBook.setBorrowed(true);
        Book savedBook = new Book();
        BeanUtils.copyProperties(testBook, savedBook);
        savedBook.setBorrowed(false);

        LoanRecord savedLoanRecord = new LoanRecord();
        BeanUtils.copyProperties(loanRecord, savedLoanRecord);
        savedLoanRecord.setReturnedAt(LocalDateTime.now());

        // When
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);
        when(loanRecordRepository.findByBookIdAndReturnedAtIsNull(TEST_BOOK_UUID))
                .thenReturn(Optional.of(loanRecord));
        when(loanRecordRepository.save(any(LoanRecord.class))).thenReturn(savedLoanRecord);

        // Then
        LoanRecord result = libraryService.returnBook(TEST_BOOK_UUID);

        assertNotNull(result);
        assertFalse(result.getBook().isBorrowed());
        assertNotNull(result.getReturnedAt());

    }

    @Test
    void returnBook_BookNotFound() {
        when(loanRecordRepository.findByBookIdAndReturnedAtIsNull(TEST_BOOK_UUID)).thenReturn(
                Optional.empty() );
        assertThrows(ResourceNotFoundException.class, () ->
            libraryService.returnBook(TEST_BOOK_UUID)
        );
        verify(loanRecordRepository).findByBookIdAndReturnedAtIsNull(TEST_BOOK_UUID);
    }
}