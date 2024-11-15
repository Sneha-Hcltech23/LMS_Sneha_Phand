package com.example.demo.LMS_Sneha_Phand.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.LMS_Sneha_Phand.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
	
	Optional<User> findByUsername(String username);
	
	boolean existsByUsername(String username);
}
