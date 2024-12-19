package com.bz.librarysystem.controller;

import com.bz.librarysystem.dto.BookDto;
import com.bz.librarysystem.service.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private LibraryService libraryService;

    @InjectMocks
    private LibraryController _underTestController;

    private BookDto mockBookDTO;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this); // Initialize mocks
        _underTestController = new LibraryController(libraryService);

        mockBookDTO = new BookDto(
                UUID.randomUUID(),
                "121232390",
                "Lord of the Rings",
                "J K Rowling",
                null);
    }


    @Test
    void testRegisterBook() {
        // Mocking behavior of BookService
        when(libraryService.registerBook(any(BookDto.class))).thenReturn(mockBookDTO);

        // Call the controller method
        ResponseEntity<BookDto> responseEntity = _underTestController.registerBook(mockBookDTO);

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockBookDTO, responseEntity.getBody());

    }

    @Test
    void testGetAllBooks() {
        // Mocking behavior of BookService

        List<BookDto> mockBooks = Collections.singletonList(mockBookDTO);
        when(libraryService.getAllBooks()).thenReturn(mockBooks);

        System.out.println("Mocked BookDTO: " + mockBookDTO);
        System.out.println("Mocked Books: " + mockBooks);

        // Call the controller method
        ResponseEntity<List<BookDto>> responseEntity = _underTestController.getAllBooks();

        System.out.println("Controller Response: " + responseEntity.getBody());

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockBooks, responseEntity.getBody());

        // Verify mock interaction
        verify(libraryService, times(1)).getAllBooks();
    }
}