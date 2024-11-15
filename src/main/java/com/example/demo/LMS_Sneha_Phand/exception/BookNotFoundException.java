package com.example.demo.LMS_Sneha_Phand.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) 
public class BookNotFoundException extends RuntimeException{
	public BookNotFoundException(String message) {
		super(message);
	}
}
