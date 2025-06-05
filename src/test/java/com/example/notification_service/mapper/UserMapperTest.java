package com.example.notification_service.mapper;

import com.example.notification_service.dto.UserDto;
import com.example.notification_service.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setup() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    /**
     * Scenario: Преобразование отдельного пользователя
     * <p>
     * Given: Valидный объект пользователя
     * When: Вызван метод toDto(User)
     * Then: Преобразованный объект UserDto содержит те же данные
     */
    @Test
    void shouldConvertSingleUserToDto() {
        User user = new User();
        user.setId(1L);
        user.setFullName("Иван Петров");
        user.setNotificationSchedule("""
                {
                    "WEDNESDAY": [
                        "08:00-13:00"
                    ],
                    "THURSDAY": [
                        "08:00-13:00"
                    ],
                    "FRIDAY": [
                        "08:00-13:00"
                    ],
                    "SATURDAY": [
                        "09:30-10:30"
                    ],
                    "SUNDAY": [
                        "16:00-20:00",
                        "21:00-22:00"
                    ]
                }
                """);

        String notificationScheduleResult = "среда-пятница с 08:00 до 13:00, суббота с 09:30 до 10:30, воскресенье с 16:00 до 20:00 и с 21:00 до 22:00";

        UserDto dto = userMapper.toDto(user);

        assertThat(dto.id()).isEqualTo(user.getId());
        assertThat(dto.fullName()).isEqualTo(user.getFullName());
        assertThat(dto.notificationSchedule()).isEqualTo(notificationScheduleResult);
    }

    /**
     * Scenario: Преобразование списка пользователей
     * <p>
     * Given: Список пользователей
     * When: Вызван метод toDto(List<User>)
     * Then: Преобразованные объекты содержат те же данные
     */
    @Test
    void shouldConvertListOfUsersToDto() {
        User user1 = new User();
        user1.setId(1L);
        user1.setFullName("Иван Петров");
        user1.setNotificationSchedule("""
                {
                    "MONDAY": [
                        "09:00-18:00"
                    ]
                }
                """);

        User user2 = new User();
        user2.setId(2L);
        user2.setFullName("Анна Иванова");
        user2.setNotificationSchedule("""
                {
                    "TUESDAY": [
                        "09:00-18:00"
                    ]
                }
                """);

        List<User> users = List.of(user1, user2);

        List<UserDto> dtos = userMapper.toDto(users);

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).id()).isEqualTo(user1.getId());
        assertThat(dtos.get(1).fullName()).isEqualTo(user2.getFullName());
    }
}