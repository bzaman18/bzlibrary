package com.bz.librarysystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.UUID;

import lombok.Data;

@Data
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "ISBN is required")
    private String isbn;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @Version
    private Long version;

    @Column(name = "is_borrowed")
    private boolean borrowed = false;
}
