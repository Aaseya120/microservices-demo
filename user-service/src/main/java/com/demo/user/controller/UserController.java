package com.demo.user.controller;

import com.demo.user.dto.Dtos.ApiResponse;
import com.demo.user.dto.Dtos.UserRequest;
import com.demo.user.dto.Dtos.UserResponse;
import com.demo.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User API", description = "Operations related to users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest req) {
        log.info("Creating user with email: {}", req.email());
        UserResponse user = userService.createUser(req);
        return ResponseEntity.status(201).body(new ApiResponse<>(user, "User created", true));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable String id) {
        log.info("Fetching user with ID: {}", id);
        return ResponseEntity.ok(new ApiResponse<>(userService.getUser(id), "OK", true));
    }

    @GetMapping
    @Operation(summary = "Get all users (paginated)")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(new ApiResponse<>(userService.getAllUsers(pageable), "OK", true));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user by ID")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        log.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>(null, "User deleted", true));
    }
}
