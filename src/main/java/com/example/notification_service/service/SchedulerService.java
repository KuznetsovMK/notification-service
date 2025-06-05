package com.example.notification_service.service;

import com.example.notification_service.entity.User;
import com.example.notification_service.web_socket.service.PrivateNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final UserService userService;
    private final PrivateNotificationService privateNotificationService;

    @Scheduled(initialDelay = 1, fixedRate = 1_000 * 60 * 10)
    public void sendWaitEvents() {
        List<User> activeUsers = userService.findActiveUsers();
        activeUsers.parallelStream().forEach(privateNotificationService::processUserEvents);
    }
}
