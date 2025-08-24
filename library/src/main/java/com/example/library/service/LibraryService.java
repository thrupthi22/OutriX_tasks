package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.Member;
import com.example.library.model.Transaction;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class LibraryService {
    private final List<Book> books = new CopyOnWriteArrayList<>();
    private final List<Member> members = new CopyOnWriteArrayList<>();
    private final List<Transaction> transactionHistory = new CopyOnWriteArrayList<>();

    public LibraryService() {
        // Initial Data
        members.add(new Member(generateId(), "Alice Johnson"));
        members.add(new Member(generateId(), "Bob Williams"));

        books.add(new Book(generateId(), "The Great Gatsby", "F. Scott Fitzgerald", "Fiction"));

        Book issuedBook = new Book(generateId(), "To Kill a Mockingbird", "Harper Lee", "Classic");
        issueBook(issuedBook, members.get(0));
        books.add(issuedBook);

        Book overdueBook = new Book(generateId(), "1984", "George Orwell", "Dystopian");
        issueBook(overdueBook, members.get(1));
        overdueBook.setIssueDate(LocalDate.now().minusDays(25));
        overdueBook.setDueDate(LocalDate.now().minusDays(10)); // This book is 10 days overdue
        books.add(overdueBook);
    }

    private String generateId() { return UUID.randomUUID().toString().substring(0, 8); }

    // --- Book Methods ---
    public List<Book> getAllBooks() {
        // Calculate fines every time books are fetched to ensure they are up-to-date
        books.forEach(this::calculateFine);
        return books;
    }

    private void calculateFine(Book book) {
        if (book.isIssued() && book.getDueDate() != null && LocalDate.now().isAfter(book.getDueDate())) {
            long daysOverdue = ChronoUnit.DAYS.between(book.getDueDate(), LocalDate.now());
            book.setFine(daysOverdue * 5); // ₹5 per day
        } else {
            book.setFine(0);
        }
    }

    public Optional<Book> findBookById(String id) { return books.stream().filter(b -> b.getId().equals(id)).findFirst(); }
    public Book addBook(String title, String author, String genre) {
        Book newBook = new Book(generateId(), title, author, genre);
        books.add(0, newBook);
        return newBook;
    }
    public boolean deleteBook(String id) {
        Optional<Book> bookOpt = findBookById(id);
        if (bookOpt.isPresent() && !bookOpt.get().isIssued()) {
            books.remove(bookOpt.get());
            transactionHistory.add(0, new Transaction(bookOpt.get().getTitle(), "System", "Deleted"));
            return true;
        }
        return false;
    }

    // --- Member Methods ---
    public List<Member> getAllMembers() { return members; }
    public Optional<Member> findMemberById(String id) { return members.stream().filter(m -> m.getId().equals(id)).findFirst(); }
    public Member addMember(String name) {
        Member newMember = new Member(generateId(), name);
        members.add(newMember);
        return newMember;
    }
    public boolean deleteMember(String id) {
        boolean hasBooksIssued = books.stream().anyMatch(b -> id.equals(b.getIssuedToMemberId()));
        if (hasBooksIssued) { return false; }
        return members.removeIf(member -> member.getId().equals(id));
    }

    // --- Transaction Methods ---
    public List<Transaction> getTransactionHistory() { return transactionHistory; }

    public Optional<Book> issueBook(String bookId, String memberId) {
        Optional<Book> bookOpt = findBookById(bookId);
        Optional<Member> memberOpt = findMemberById(memberId);
        if (bookOpt.isPresent() && memberOpt.isPresent() && !bookOpt.get().isIssued()) {
            Book book = bookOpt.get();
            issueBook(book, memberOpt.get());
            return Optional.of(book);
        }
        return Optional.empty();
    }

    public Optional<Book> returnBook(String bookId) {
        Optional<Book> bookOpt = findBookById(bookId);
        if (bookOpt.isPresent() && bookOpt.get().isIssued()) {
            Book book = bookOpt.get();
            Optional<Member> memberOpt = findMemberById(book.getIssuedToMemberId());
            String memberName = memberOpt.map(Member::getName).orElse("Unknown");

            book.setIssued(false);
            book.setIssueDate(null);
            book.setDueDate(null);
            book.setIssuedToMemberId(null);
            book.setFine(0);
            transactionHistory.add(0, new Transaction(book.getTitle(), memberName, "Returned"));
            return Optional.of(book);
        }
        return Optional.empty();
    }

    private void issueBook(Book book, Member member) {
        book.setIssued(true);
        book.setIssuedToMemberId(member.getId());
        book.setIssueDate(LocalDate.now());
        book.setDueDate(LocalDate.now().plusDays(15));
        transactionHistory.add(0, new Transaction(book.getTitle(), member.getName(), "Issued"));
    }
}