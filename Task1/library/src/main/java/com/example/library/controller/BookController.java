package com.example.library.controller;

import com.example.library.model.Book;
import com.example.library.model.Member;
import com.example.library.model.Transaction;
import com.example.library.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class BookController {

    private final LibraryService libraryService;

    @Autowired
    public BookController(LibraryService libraryService) { this.libraryService = libraryService; }

    // Book Endpoints
    @GetMapping("/books")
    public List<Book> getAllBooks() { return libraryService.getAllBooks(); }

    @PostMapping("/books")
    public Book addBook(@RequestBody Map<String, String> payload) {
        return libraryService.addBook(payload.get("title"), payload.get("author"), payload.get("genre"));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        return libraryService.deleteBook(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/books/issue")
    public ResponseEntity<Book> issueBook(@RequestBody Map<String, String> payload) {
        Optional<Book> book = libraryService.issueBook(payload.get("bookId"), payload.get("memberId"));
        return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/books/return")
    public ResponseEntity<Book> returnBook(@RequestBody Map<String, String> payload) {
        Optional<Book> book = libraryService.returnBook(payload.get("bookId"));
        return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    // Member Endpoints
    @GetMapping("/members")
    public List<Member> getAllMembers() { return libraryService.getAllMembers(); }

    @PostMapping("/members")
    public Member addMember(@RequestBody Map<String, String> payload) {
        return libraryService.addMember(payload.get("name"));
    }

    @DeleteMapping("/members/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable String id) {
        boolean deleted = libraryService.deleteMember(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Conflict if member has books
    }

    // History Endpoint
    @GetMapping("/history")
    public List<Transaction> getHistory() { return libraryService.getTransactionHistory(); }
}