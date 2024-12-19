package com.bz.librarysystem.controller;


import com.bz.librarysystem.LibraryApplication;
import com.bz.librarysystem.dto.BookDto;
import com.bz.librarysystem.dto.BorrowerDto;
import com.bz.librarysystem.entity.Book;
import com.bz.librarysystem.entity.Borrower;
import com.bz.librarysystem.entity.LoanRecord;
import com.bz.librarysystem.service.LibraryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(classes = LibraryApplication.class)
@AutoConfigureMockMvc
class LibraryControllerIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LibraryService libraryService;

    private BookDto testBookDTO;

    private BorrowerDto borrowerDTO;

    private LoanRecord loanRecord;

    private static final UUID TEST_BOOK_UUDI = UUID.randomUUID();
    private static final UUID TEST_BORROWER_UUDI = UUID.randomUUID();
    private static final UUID TEST_LOAN_ID =UUID.randomUUID() ;

    private static final LocalDateTime borrowDate = LocalDateTime.now();


    @BeforeEach
    void setUp() {
        // Sample BookDTO for testing
        testBookDTO = new BookDto();
        testBookDTO.setId(TEST_BOOK_UUDI);
        testBookDTO.setTitle("Sample Book");
        testBookDTO.setAuthor("John Doe");
        testBookDTO.setIsbn("1234567890");

        borrowerDTO = new BorrowerDto();
        borrowerDTO.setEmail("jg@gmail.com");
        borrowerDTO.setName("JK Rowling");
        borrowerDTO.setId(TEST_BORROWER_UUDI);

        Borrower borrower = new Borrower();
        borrower.setId(TEST_BORROWER_UUDI);
        borrower.setName("Joe Bloggs");
        borrower.setName("joe@bloggs.com");

        Book book = new Book();
        BeanUtils.copyProperties(testBookDTO,book);

        loanRecord = new LoanRecord();
        loanRecord.setId(TEST_LOAN_ID);
        loanRecord.setBorrower(borrower);
        loanRecord.setBook(book);
        loanRecord.setBorrowedAt(borrowDate);
    }

    @Test
    void testRegisterBook() throws Exception {
        // Mocking the service method to return the testBookDTO when called
        Mockito.when(libraryService.registerBook(any(BookDto.class))).thenReturn(testBookDTO);

        // Perform POST request to /api/books with testBookDTO as JSON
        ResultActions resultActions = mockMvc.perform(post("/api/v1/library/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookDTO)));

        // Verify the response
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testBookDTO.getId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(testBookDTO.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author").value(testBookDTO.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isbn").value(testBookDTO.getIsbn()));
    }

    @Test
    void testGetAllBooks() throws Exception {
        // Mocking the service method to return a list of testBookDTOs when called
        List<BookDto> bookDTOList = Collections.singletonList(testBookDTO);
        Mockito.when(libraryService.getAllBooks()).thenReturn(bookDTOList);

        // Perform GET request to /api/books
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/library/books"));

        // Verify the response
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(testBookDTO.getId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value(testBookDTO.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].author").value(testBookDTO.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].isbn").value(testBookDTO.getIsbn()));
    }

    @Test
    void registerBorrowerTest() throws Exception {
        // Act
        Mockito.when(libraryService.registerBorrower(any(BorrowerDto.class))).thenReturn(borrowerDTO);

        // Perform POST request to /api/books with testBookDTO as JSON
        ResultActions resultActions = mockMvc.perform(post("/api/v1/library/borrowers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(borrowerDTO)));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(borrowerDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(borrowerDTO.getEmail()));

    }

    @Test
    void borrowBookTest() throws Exception {
        // Arrange
        Mockito.when(libraryService.borrowBook(any(UUID.class),any(UUID.class)))
                .thenReturn(loanRecord);

        ResultActions resultActions = mockMvc.perform(post("/api/v1/library/books/{bookId}/borrow/{borrowerId}", TEST_BOOK_UUDI, TEST_BORROWER_UUDI)
                .contentType(MediaType.APPLICATION_JSON));

        String borrowDateString = borrowDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);


        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(loanRecord.getId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.book.id").value(TEST_BOOK_UUDI.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.book.isbn").value("1234567890"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.book.title").value("Sample Book"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.borrower.id").value(TEST_BORROWER_UUDI.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.borrower.name").value("joe@bloggs.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.borrowedAt").value(Matchers.startsWith(loanRecord.getBorrowedAt().toString().substring(0, 23))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.borrowedAt").value(borrowDateString))
                .andExpect(MockMvcResultMatchers.jsonPath("$.returnedAt").isEmpty());

        // Verify the interaction with the service
        Mockito.verify(libraryService, Mockito.times(1)).borrowBook(TEST_BOOK_UUDI, TEST_BORROWER_UUDI);

    }
}

