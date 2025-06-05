package com.example.notification_service.service;

import com.example.notification_service.entity.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ScheduleHelperTest {

    @InjectMocks
    private ScheduleHelper helper;


    /**
     * Scenario: Проверка активности пользователя в рабочее время
     * <p>
     * Given: Пользователь с рабочим расписанием на понедельник с 09:00 до 18:00
     * And: Текущее время — понедельник, 12:00
     * When: Вызван метод isUserActive()
     * Then: Пользователь считается активным
     */
    @SneakyThrows
    @Test
    void shouldBeActiveDuringWorkingHours() {
        User user = new User();
        user.setNotificationSchedule("""
                {
                    "MONDAY": ["09:00-18:00"]
                }
                """);
        LocalDateTime now = LocalDateTime.of(2025, 6, 2, 12, 0);

        boolean isActive = helper.isUserActive(user, now);

        assertThat(isActive).isTrue();
    }


    /**
     * Scenario: Проверка неактивности пользователя вне рабочего времени
     * <p>
     * Given: Пользователь с рабочим расписанием на понедельник с 09:00 до 18:00
     * And: Текущее время — понедельник, 19:00
     * When: Вызван метод isUserActive()
     * Then: Пользователь считается неактивным
     */

    @Test
    void shouldNotBeActiveOutsideWorkingHours() {
        User user = new User();
        user.setNotificationSchedule("""
                {
                    "MONDAY": ["09:00-18:00"]
                }
                """);
        LocalDateTime now = LocalDateTime.of(2025, 6, 2, 19, 0);

        boolean isActive = helper.isUserActive(user, now);

        assertThat(isActive).isFalse();
    }


    /**
     * Scenario: Проверка реакции на некорректный формат расписания
     * <p>
     * Given: Пользователь с расписанием в некорректном формате
     * When: Вызван метод isUserActive()
     * Then: Генерируется исключение
     */

    @Test
    void shouldFailOnInvalidScheduleFormat() {
        User user = new User();
        user.setNotificationSchedule("Некорректный JSON");
        LocalDateTime now = LocalDateTime.of(2025, 6, 2, 12, 0);

        assertThatThrownBy(() -> helper.isUserActive(user, now))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Ошибка при проверке активности пользователя");
    }


    /**
     * Scenario: Проверка неактивности пользователя в субботу при расписании на будний день
     * <p>
     * Given: Пользователь с расписанием на понедельник с 09:00 до 18:00
     * And: Текущее время — суббота, 12:00
     * When: Вызван метод isUserActive()
     * Then: Пользователь считается неактивным
     */

    @Test
    void shouldNotBeActiveOnSaturdayWithoutSaturdaySchedule() {
        User user = new User();
        user.setNotificationSchedule("""
                {
                    "MONDAY": ["09:00-18:00"]
                }
                """);
        LocalDateTime now = LocalDateTime.of(2025, 6, 7, 12, 0);

        boolean isActive = helper.isUserActive(user, now);

        assertThat(isActive).isFalse();
    }
}