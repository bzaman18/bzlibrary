package com.bz.librarysystem.controller;

import com.bz.librarysystem.dto.BookDto;
import com.bz.librarysystem.dto.BookInventoryDto;
import com.bz.librarysystem.dto.BorrowerDto;
import com.bz.librarysystem.service.LibraryService;
import com.bz.librarysystem.service.impl.LibraryServiceImpl;
import com.bz.librarysystem.entity.Book;
import com.bz.librarysystem.entity.Borrower;
import com.bz.librarysystem.entity.LoanRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/library")
@Tag(name = "Library Management", description = "Library management APIs")
@CrossOrigin(origins = "*")
public class LibraryController {
    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @Operation(
            summary = "Register a new book",
            description = "Adds a new book to the library system. Multiple books with same ISBN are allowed."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book registered successfully",
                    content = @Content(schema = @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "400", description = "Invalid book data provided")
    })
    @PostMapping("/books")
    public ResponseEntity<BookDto> registerBook(
            @Parameter(description = "Book details") @Valid @RequestBody BookDto book) {
        return ResponseEntity.ok(libraryService.registerBook(book));
    }

    @Operation(
            summary = "Register a new borrower",
            description = "Registers a new borrower in the library system. Email must be unique."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Borrower registered successfully",
                    content = @Content(schema = @Schema(implementation = Borrower.class))),
            @ApiResponse(responseCode = "400", description = "Invalid borrower data or email already exists")
    })
    @PostMapping("/borrowers")
    public ResponseEntity<BorrowerDto> registerBorrower(
            @Parameter(description = "Borrower details") @Valid @RequestBody BorrowerDto borrower) {
        return ResponseEntity.ok(libraryService.registerBorrower(borrower));
    }

    @Operation(
            summary = "Borrow a book",
            description = "Records a book being borrowed by a borrower. Ensures only one borrower can borrow a specific book at a time."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book borrowed successfully",
                    content = @Content(schema = @Schema(implementation = LoanRecord.class))),
            @ApiResponse(responseCode = "404", description = "Book or borrower not found"),
            @ApiResponse(responseCode = "400", description = "Book is already borrowed")
    })
    @PostMapping("/books/{bookId}/borrow/{borrowerId}")
    public ResponseEntity<LoanRecord> borrowBook(
            @Parameter(description = "ID of the book to borrow") @PathVariable UUID bookId,
            @Parameter(description = "ID of the borrower") @PathVariable UUID borrowerId) {
        return ResponseEntity.ok(libraryService.borrowBook(bookId, borrowerId));
    }

    @Operation(
            summary = "Return a book",
            description = "Records a borrowed book being returned to the library."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book returned successfully",
                    content = @Content(schema = @Schema(implementation = LoanRecord.class))),
            @ApiResponse(responseCode = "404", description = "No active loan found for this book")
    })
    @PostMapping("/books/{bookId}/return")
    public ResponseEntity<LoanRecord> returnBook(
            @Parameter(description = "ID of the book to return") @PathVariable UUID bookId) {
        return ResponseEntity.ok(libraryService.returnBook(bookId));
    }

    @Operation(
            summary = "Get all books",
            description = "Retrieves a list of all books in the library system."
    )
    @ApiResponse(responseCode = "200", description = "List of books retrieved successfully",
            content = @Content(schema = @Schema(implementation = Book.class)))
    @GetMapping("/books")
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return ResponseEntity.ok(libraryService.getAllBooks());
    }


    @GetMapping("/books/inventory")
    public ResponseEntity<List<BookInventoryDto>> getAllBooksInventory() {
        return ResponseEntity.ok(libraryService.getBookInventory());
    }

}

