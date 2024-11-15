package com.example.demo.library_management_system.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.LMS_Sneha_Phand.service.BookService;
import com.example.demo.LMS_Sneha_Phand.entity.Book;
import com.example.demo.LMS_Sneha_Phand.entity.BorrowedBook;
import com.example.demo.LMS_Sneha_Phand.entity.User;
import com.example.demo.LMS_Sneha_Phand.entity.Book.BookStatus;
import com.example.demo.LMS_Sneha_Phand.exception.BookNotAvailableException;
import com.example.demo.LMS_Sneha_Phand.exception.BookNotFoundException;
import com.example.demo.LMS_Sneha_Phand.exception.UserNotFoundException;
import com.example.demo.LMS_Sneha_Phand.repository.BookRepository;
import com.example.demo.LMS_Sneha_Phand.repository.BorrowedBookRepository;
import com.example.demo.LMS_Sneha_Phand.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepo;

    @Mock
    private BorrowedBookRepository borrowedBookRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private User user;
    private BorrowedBook borrowedBook;

    @BeforeEach
    void setUp() {
        // Initialize Book
        book = new Book();
        book.setBookId("1");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setGenre("Fiction");
        book.setAvailableCopies(1);
        book.setStatus(BookStatus.AVAILABLE);

        // Initialize User
        user = new User();
        user.setUserId("u1");
        user.setUsername("testuser");

        // Initialize BorrowedBook
        borrowedBook = new BorrowedBook();
        borrowedBook.setBook(book);
        borrowedBook.setUser(user);
        borrowedBook.setBorrowDate(LocalDate.now());
        borrowedBook.setDueDate(LocalDate.now().plusDays(14));
    }

    // Test for fetching a book successfully
    @Test
    void testGetBook_Success() {
        List<Book> books = List.of(book);

        when(bookRepo.findByTitleContainingOrAuthorContainingOrGenreContaining(
                anyString(), anyString(), anyString())).thenReturn(books);

        List<Book> result = bookService.getbook("Test", "Author", "Fiction");

        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
    }

    // Test for book not found
    @Test
    void testGetBook_NotFound() {
        when(bookRepo.findByTitleContainingOrAuthorContainingOrGenreContaining(
                anyString(), anyString(), anyString())).thenReturn(new ArrayList<>());

        assertThrows(BookNotFoundException.class, () -> bookService.getbook("Nonexistent", "Author", "Genre"));
    }

    // Test for successfully borrowing a book
    @Test
    void testBorrowBook_Success() {
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(bookRepo.findById("1")).thenReturn(Optional.of(book));

        String dueDate = bookService.borrowBook("1", "testuser");

        assertEquals(LocalDate.now().plusDays(14).toString(), dueDate);
        assertEquals(0, book.getAvailableCopies());
        assertEquals(BookStatus.BORROWED, book.getStatus());
        verify(borrowedBookRepo, times(1)).save(any(BorrowedBook.class));
    }

    // Test for borrowing a book when no copies are available
    @Test
    void testBorrowBook_BookNotAvailable() {
        book.setAvailableCopies(0);

        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(bookRepo.findById("1")).thenReturn(Optional.of(book));

        assertThrows(BookNotAvailableException.class, () -> bookService.borrowBook("1", "testuser"));
    }

    // Test for successfully retrieving the borrowing history
    @Test
    void testGetBorrowingHistory_Success() {
        List<BorrowedBook> borrowings = List.of(borrowedBook);

        when(userRepo.findById("u1")).thenReturn(Optional.of(user));
        when(borrowedBookRepo.findByUserId("u1")).thenReturn(borrowings);

        List<Map<String, Object>> history = bookService.getBorrowingHistory("u1");

        assertEquals(1, history.size());
        assertEquals("Test Book", history.get(0).get("title"));
    }

    // Test for user not found when retrieving borrowing history
    @Test
    void testGetBorrowingHistory_UserNotFound() {
        when(userRepo.findById("u1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookService.getBorrowingHistory("u1"));
    }

    // Test for successfully returning a book
    @Test
    void testReturnBook_Success() {
        when(userRepo.findById("u1")).thenReturn(Optional.of(user));
        when(bookRepo.findById("1")).thenReturn(Optional.of(book));
        when(borrowedBookRepo.findByUser_UserIdAndBook_BookId("u1", "1"))
                .thenReturn(Optional.of(borrowedBook));

        String result = bookService.returnBook("u1", "1");

        assertEquals("Book returned successfully.", result);
        assertEquals(BookStatus.AVAILABLE, book.getStatus());
        assertEquals(1, book.getAvailableCopies());
        assertNotNull(borrowedBook.getReturnDate());
    }

    // Test for user not found when returning a book
    @Test
    void testReturnBook_UserNotFound() {
        when(userRepo.findById("u1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookService.returnBook("u1", "1"));
    }

    // Test for book not found when returning a book
    @Test
    void testReturnBook_BookNotFound() {
        when(userRepo.findById("u1")).thenReturn(Optional.of(user));
        when(bookRepo.findById("1")).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.returnBook("u1", "1"));
    }
}
