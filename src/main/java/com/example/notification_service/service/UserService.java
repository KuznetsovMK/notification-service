package com.example.notification_service.service;


import com.example.notification_service.entity.User;
import com.example.notification_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ScheduleHelper scheduleHelper;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public User normalizeUserNotificationScheduleAndSave(User user) {
        scheduleHelper.normalizeUserSchedule(user);
        return userRepository.save(user);
    }

    public List<User> findActiveUsers() {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .filter(user -> isUserActive(user, LocalDateTime.now()))
                .toList();
    }

    public boolean isUserActive(User user, LocalDateTime currentDateTime) {
        return scheduleHelper.isUserActive(user, currentDateTime);
    }
}
