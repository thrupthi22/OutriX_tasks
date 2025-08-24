package com.example.library.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Book {
    private String id;
    private String title;
    private String author;
    private String genre;
    private boolean isIssued;
    private String issuedToMemberId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private long fine; // <-- NEW: Fine is now calculated on the backend

    public Book(String id, String title, String author, String genre) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isIssued = false;
    }
}