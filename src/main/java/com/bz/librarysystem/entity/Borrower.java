package com.bz.librarysystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.UUID;

import lombok.Data;

@Data
@Entity
@Table(name = "borrowers")
public class Borrower {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(unique = true)
    private String email;
}
