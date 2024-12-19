package com.bz.librarysystem.exception;

public class DuplicateBorrowerEmailException extends RuntimeException {
    public DuplicateBorrowerEmailException(String message) {
        super(message);
    }
}