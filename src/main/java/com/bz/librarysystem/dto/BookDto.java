package com.bz.librarysystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
    private UUID id;
    private String isbn;
    private String title;
    private String author;
    private Long version;
}