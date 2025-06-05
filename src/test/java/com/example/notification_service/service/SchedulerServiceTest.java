package com.example.notification_service.service;


import com.example.notification_service.entity.User;
import com.example.notification_service.web_socket.service.PrivateNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class SchedulerServiceTest {

    @InjectMocks
    private SchedulerService schedulerService;

    @Mock
    private UserService userService;

    @Mock
    private PrivateNotificationService privateNotificationService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Scenario: Обработка событий для активных пользователей
     * <p>
     * Given: Существует несколько активных пользователей
     * When: Выполняется запланированная задача
     * Then: Происходит обработка событий для каждого активного пользователя
     */
    @Test
    void shouldProcessUserEvents() {
        List<User> activeUsers = List.of(new User(), new User());
        given(userService.findActiveUsers()).willReturn(activeUsers);

        schedulerService.sendWaitEvents();

        verify(privateNotificationService, times(2)).processUserEvents(any(User.class));
    }

    /**
     * Scenario: Обработка событий при отсутствии активных пользователей
     * <p>
     * Given: Нет активных пользователей
     * When: Выполняется запланированная задача
     * Then: Никакие события не отправляются
     */
    @Test
    void shouldHandleEmptyUserListGracefully() {
        List<User> emptyUsers = List.of();
        given(userService.findActiveUsers()).willReturn(emptyUsers);

        schedulerService.sendWaitEvents();

        verify(privateNotificationService, never()).processUserEvents(any(User.class));
    }
}
