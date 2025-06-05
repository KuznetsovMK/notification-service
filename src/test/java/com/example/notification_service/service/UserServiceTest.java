package com.example.notification_service.service;

import com.example.notification_service.entity.User;
import com.example.notification_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ScheduleHelper scheduleHelper;

    /**
     * Scenario: Поиск всех пользователей
     * <p>
     * Given: Репозиторий возвращает список пользователей
     * When: Вызван метод findAll()
     * Then: Возвращается полный список пользователей
     */
    @Test
    void shouldReturnAllUsers() {
        List<User> users = List.of(new User(), new User());
        given(userRepository.findAll()).willReturn(users);

        List<User> retrievedUsers = userService.findAll();

        assertThat(retrievedUsers).isEqualTo(users);
    }

    /**
     * Scenario: Поиск пользователя по ID
     * <p>
     * Given: Пользователь с указанным ID существует
     * When: Вызван метод findById()
     * Then: Возвращается пользователь с указанным ID
     */
    @Test
    void shouldFindUserById() {
        User user = new User();
        user.setId(1L);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        Optional<User> retrievedUser = userService.findById(1L);

        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get()).isEqualTo(user);
    }

    /**
     * Scenario: Удаление пользователя по ID
     * <p>
     * Given: Пользователь с указанным ID существует
     * When: Вызван метод deleteById()
     * Then: Пользователь удаляется из репозитория
     */
    @Test
    void shouldDeleteUserById() {
        Long userId = 1L;

        userService.deleteById(userId);

        verify(userRepository).deleteById(userId);
    }

    /**
     * Scenario: Нормализация расписания и сохранение пользователя
     * <p>
     * Given: Пользователь с ненормализованным расписанием
     * When: Вызван метод normalizeUserNotificationScheduleAndSave()
     * Then: Пользователь сохраняется с нормализованным расписанием
     */
    @Test
    void shouldNormalizeAndSaveUser() {
        User user = new User();
        user.setNotificationSchedule("{\"MONDAY\": [\"09:00-18:00\"]}");

        doNothing().when(scheduleHelper).normalizeUserSchedule(user);
        given(userRepository.save(user)).willReturn(user);

        User savedUser = userService.normalizeUserNotificationScheduleAndSave(user);

        assertThat(savedUser).isEqualTo(user);
    }

    /**
     * Scenario: Поиск активных пользователей
     * <p>
     * Given: Есть несколько пользователей, один из которых активен
     * When: Вызван метод findActiveUsers()
     * Then: Возвращается список активных пользователей
     */
    @Test
    void shouldFindActiveUsers() {
        User activeUser = new User();
        activeUser.setId(1L);
        User inactiveUser = new User();
        inactiveUser.setId(2L);
        List<User> users = List.of(activeUser, inactiveUser);
        given(userRepository.findAll()).willReturn(users);
        given(scheduleHelper.isUserActive(eq(activeUser), any(LocalDateTime.class))).willReturn(true);
        given(scheduleHelper.isUserActive(eq(inactiveUser), any(LocalDateTime.class))).willReturn(false);

        List<User> activeUsers = userService.findActiveUsers();

        assertThat(activeUsers).containsOnly(activeUser);
    }

    /**
     * Scenario: Проверка активности пользователя
     * <p>
     * Given: Пользователь с расписанием
     * When: Вызван метод isUserActive()
     * Then: Возвращается true, если пользователь активен согласно расписанию
     */
    @Test
    void shouldCheckUserActivity() {
        User user = new User();
        user.setNotificationSchedule("{\"MONDAY\": [\"09:00-18:00\"]}");
        LocalDateTime now = LocalDateTime.of(2025, 6, 2, 12, 0);
        given(scheduleHelper.isUserActive(user, now)).willReturn(true);

        boolean isActive = userService.isUserActive(user, now);

        assertThat(isActive).isTrue();
    }
}