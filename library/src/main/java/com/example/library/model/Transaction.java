package com.example.library.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Transaction {
    private final String bookTitle;
    private final String memberName; // <-- NEW FIELD
    private final String action;
    private final LocalDateTime timestamp;

    public Transaction(String bookTitle, String memberName, String action) {
        this.bookTitle = bookTitle;
        this.memberName = memberName;
        this.action = action;
        this.timestamp = LocalDateTime.now();
    }
}

