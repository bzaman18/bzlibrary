package com.bz.librarysystem.dto;

import lombok.Data;

@Data
public class BookInventoryDto {
    private final String isbn;
    private final String title;
    private final String author;
    private final int numberOfCopies;
}