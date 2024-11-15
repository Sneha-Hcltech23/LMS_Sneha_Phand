package com.example.demo.library_management_system.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.demo.LMS_Sneha_Phand.controller.BookController;
import com.example.demo.LMS_Sneha_Phand.entity.Book;
import com.example.demo.LMS_Sneha_Phand.exception.UserNotFoundException;
import com.example.demo.LMS_Sneha_Phand.jwt.JwtService;
import com.example.demo.LMS_Sneha_Phand.repository.BookRepository;
import com.example.demo.LMS_Sneha_Phand.repository.UserRepository;
import com.example.demo.LMS_Sneha_Phand.service.BookService;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    public void testSearchBook_Found() throws Exception {
        // Arrange
        Book book = new Book();
        book.setBookId("1");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setGenre("Fiction");

        // Arrange: setting up the mock behavior
        when(bookService.getbook(anyString(), anyString(), anyString())).thenReturn(List.of(book));

        // Act: perform the GET request to search for a book
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/search")
                .param("title", "Test Book")
                .param("author", "Test Author")
                .param("genre", "Fiction"))
                
                // Assert: check the response status and body
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Test Book"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].author").value("Test Author"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].genre").value("Fiction"));

        // Verify the interaction with the service
        verify(bookService, times(1)).getbook(anyString(), anyString(), anyString());
    }

    @Test
    public void testSearchBook_NotFound() throws Exception {
        // Arrange: no books found based on search criteria
        when(bookService.getbook(anyString(), anyString(), anyString())).thenReturn(Collections.emptyList());

        // Act: perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/search")
                .param("title", "Nonexistent Book")
                .param("author", "Nonexistent Author")
                .param("genre", "Nonexistent Genre"))
                
                // Assert: check for a 404 (Not Found) status
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        // Verify the interaction with the service
        verify(bookService, times(1)).getbook(anyString(), anyString(), anyString());
    }

    @Test
    public void testAddBook_Success() throws Exception {
        // Arrange: create a new book and set up mock behavior
        Book newBook = new Book();
        newBook.setBookId("2");
        newBook.setTitle("New Book");
        newBook.setAuthor("New Author");
        newBook.setGenre("Science Fiction");

        when(bookService.addBook(any(Book.class))).thenReturn(newBook);

        // Act: perform the POST request to add a new book
        mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                .contentType("application/json")
                .content("{\"title\":\"New Book\", \"author\":\"New Author\", \"genre\":\"Science Fiction\"}"))
                
                // Assert: check the response status and body
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("New Book"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author").value("New Author"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value("Science Fiction"));

        // Verify the interaction with the service
        verify(bookService, times(1)).addBook(any(Book.class));
    }

    @Test
    public void testAddBook_Failure() throws Exception {
        // Arrange: simulate an error while adding a book (e.g., invalid input)
        when(bookService.addBook(any(Book.class))).thenThrow(new IllegalArgumentException("Invalid input"));

        // Act: perform the POST request to add a new book with invalid data
        mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                .contentType("application/json")
                .content("{\"title\":\"\", \"author\":\"New Author\", \"genre\":\"Science Fiction\"}"))
                
                // Assert: check for a 400 (Bad Request) status due to invalid input
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid input"));

        // Verify the interaction with the service
        verify(bookService, times(1)).addBook(any(Book.class));
    }
}