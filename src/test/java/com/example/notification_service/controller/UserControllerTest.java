package com.example.notification_service.controller;

import com.example.notification_service.dto.UserDto;
import com.example.notification_service.entity.User;
import com.example.notification_service.mapper.UserMapper;
import com.example.notification_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    /**
     * Scenario: Получение всех пользователей
     * <p>
     * Given: Есть три зарегистрированных пользователя
     * When: Клиент запрашивает всех пользователей
     * Then: Должен вернуться список из трёх пользователей с кодом OK
     */
    @Test
    void shouldReturnAllUsers() {
        List<User> users = List.of(createDummyUser(1L), createDummyUser(2L), createDummyUser(3L));
        given(userService.findAll()).willReturn(users);
        given(userMapper.toDto(users)).willReturn(List.of(
                new UserDto(1L, "Иван Иванов", "{}"),
                new UserDto(2L, "Анна Петрова", "{}"),
                new UserDto(3L, "Алексей Сидоров", "{}")
        ));

        ResponseEntity<List<UserDto>> response = restTemplate.exchange(
                "/api/users/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserDto>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(3);
    }

    /**
     * Scenario: Получение пользователя по ID
     * <p>
     * Given: Существует пользователь с известным ID
     * When: Запрашивается конкретный пользователь по его ID
     * Then: Возвращается соответствующий пользователь с кодом OK
     */
    @Test
    void shouldReturnUserById() {
        long validId = 1L;
        User dummyUser = createDummyUser(validId);
        given(userService.findById(validId)).willReturn(Optional.of(dummyUser));
        given(userMapper.toDto(dummyUser)).willReturn(new UserDto(validId, "Иван Иванов", "{}"));

        ResponseEntity<UserDto> response = restTemplate.getForEntity("/api/users/" + validId, UserDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    /**
     * Scenario: Создание нового пользователя
     * <p>
     * Given: Валидная форма регистрации нового пользователя
     * When: Посылается запрос на регистрацию пользователя
     * Then: Возвращается ответ с кодом CREATED и ссылка на созданный ресурс
     */
    @Test
    void shouldCreateUser() {
        User inputUser = createDummyUser(null);
        User createdUser = createDummyUser(1L);
        given(userService.normalizeUserNotificationScheduleAndSave(any())).willReturn(createdUser);

        RequestEntity<User> request = RequestEntity.post(URI.create("/api/users/"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(inputUser);

        ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    /**
     * Scenario: Обновление существующих данных пользователя
     * <p>
     * Given: Существует пользователь с известными данными
     * When: Отправлен запрос на обновление пользователя
     * Then: Данные пользователя обновляются и возвращаются с кодом OK
     */
    @Test
    void shouldUpdateUser() {
        long validId = 1L;
        User updatedUser = createDummyUser(validId);
        given(userService.findById(validId)).willReturn(Optional.of(updatedUser));
        given(userService.normalizeUserNotificationScheduleAndSave(updatedUser)).willReturn(updatedUser);
        given(userMapper.toDto(updatedUser)).willReturn(new UserDto(validId, "Новое имя", "{}"));

        RequestEntity<User> request = RequestEntity.put(URI.create("/api/users/" + validId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(updatedUser);

        ResponseEntity<UserDto> response = restTemplate.exchange(request, UserDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    /**
     * Scenario: Удаление пользователя
     * <p>
     * Given: Существует пользователь с известным ID
     * When: Отправлен запрос на удаление пользователя
     * Then: Пользователь удаляется, возвращается ответ с кодом NO_CONTENT
     */
    @Test
    void shouldDeleteUser() {
        long validId = 1L;

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/users/" + validId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    /**
     * Scenario: Получение активных пользователей
     * <p>
     * Given: Существуют активные пользователи
     * When: Запрошен список активных пользователей
     * Then: Возвращается список активных пользователей с кодом OK
     */
    @Test
    void shouldFindActiveUsers() {
        List<User> activeUsers = Collections.singletonList(createDummyUser(1L));
        given(userService.findActiveUsers()).willReturn(activeUsers);
        given(userMapper.toDto(activeUsers)).willReturn(Collections.singletonList(
                new UserDto(1L, "Иван Иванов", "{}")
        ));

        ResponseEntity<List<UserDto>> response = restTemplate.exchange(
                "/api/users/active",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserDto>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    private User createDummyUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setFullName("Иван Иванов");
        user.setNotificationSchedule("{}");
        return user;
    }
}