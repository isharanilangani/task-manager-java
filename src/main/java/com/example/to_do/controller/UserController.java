package com.example.to_do.controller;

import com.example.to_do.dto.LoginResponse;
import com.example.to_do.model.User;
import com.example.to_do.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            // Log validation errors
            StringBuilder errors = new StringBuilder("Validation failed: ");
            result.getAllErrors().forEach(error -> errors.append(error.getDefaultMessage()).append("; "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
        }

        if (userService.emailExists(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already in use.");
        }

        userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String email, @RequestParam String password) {
        User user = userService.loginUser(email, password);
        if (user != null) {
            LoginResponse response = new LoginResponse(user.getId(), user.getEmail());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
    }

    @GetMapping("/name/{id}")
    public ResponseEntity<?> getUserNameById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user.getName());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    }
}