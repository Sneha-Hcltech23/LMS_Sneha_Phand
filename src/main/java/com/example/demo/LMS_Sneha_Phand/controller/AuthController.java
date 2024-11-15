package com.example.demo.LMS_Sneha_Phand.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.LMS_Sneha_Phand.entity.User;
import com.example.demo.LMS_Sneha_Phand.jwt.JwtService;
import com.example.demo.LMS_Sneha_Phand.repository.BorrowedBookRepository;
import com.example.demo.LMS_Sneha_Phand.repository.UserRepository;
import com.example.demo.LMS_Sneha_Phand.service.BookService;
import com.example.demo.LMS_Sneha_Phand.service.UserService;



@RestController
@RequestMapping("/api/users")
public class AuthController {

	@Autowired
	JwtService jwtService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	BorrowedBookRepository borrowedBookRepository;

	@Autowired
	BookService bookService;

	@Autowired
	UserService userService;

	@Autowired
	AuthenticationManager authenticationManager;

	@PostMapping("/authenticate")
	public String generateJWTToken(@RequestParam("username") String username,
	@RequestParam("password") String password) {
	Authentication authentication = authenticationManager.authenticate(
	new UsernamePasswordAuthenticationToken(username,password));
	SecurityContextHolder.getContext().setAuthentication(authentication);
	if (authentication.isAuthenticated()) {
	// return jwtService.generateToken(username);
	String token = jwtService.generateToken(username);
	        System.out.println("Generated Token: " + token); // Log it for debugging
	        return token;
	 
	} else {
	 
	throw new UsernameNotFoundException("Invalid user request!!!");
	 
	}
	}
}

	

