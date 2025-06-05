package com.example.notification_service.controller;


import com.example.notification_service.dto.UserDto;
import com.example.notification_service.entity.User;
import com.example.notification_service.mapper.UserMapper;
import com.example.notification_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/")
    public ResponseEntity<List<UserDto>> listUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(userMapper.toDto(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userService.findById(id);
        return userOptional
                .map(user -> ResponseEntity.ok(userMapper.toDto(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<Void> createUser(@Valid @RequestBody User user) {
        User savedUser = userService.normalizeUserNotificationScheduleAndSave(user);
        URI location = URI.create("/api/users/" + savedUser.getId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody User updatedUser) {
        Optional<User> existingUserOpt = userService.findById(id);
        if (existingUserOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User existingUser = existingUserOpt.get();
        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setNotificationSchedule(updatedUser.getNotificationSchedule());

        User saved = userService.normalizeUserNotificationScheduleAndSave(existingUser);
        return ResponseEntity.ok(userMapper.toDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserDto>> findActive() {
        List<User> activeUsers = userService.findActiveUsers();
        return ResponseEntity.ok(userMapper.toDto(activeUsers));
    }
}
