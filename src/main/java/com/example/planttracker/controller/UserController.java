package com.example.planttracker.controller;

import com.example.planttracker.model.User;
import com.example.planttracker.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // -----------------------------
    // Register a new user
    // -----------------------------
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        // Check if username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        // Save the new user
        return userRepository.save(user);
    }

    // -----------------------------
    // Login
    // -----------------------------
    @PostMapping("/login")
    public User login(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser.isPresent() &&
                existingUser.get().getPassword().equals(user.getPassword())) { // Simple password check
            return existingUser.get();
        } else {
            throw new RuntimeException("Invalid username or password");
        }
    }
}