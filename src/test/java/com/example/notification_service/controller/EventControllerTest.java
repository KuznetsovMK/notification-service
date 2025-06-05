package com.example.notification_service.controller;

import com.example.notification_service.entity.Event;
import com.example.notification_service.repository.EventRepository;
import com.example.notification_service.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private EventService eventService;

    @MockBean
    private EventRepository eventRepository;

    /**
     * Scenario: Получение всех событий
     * <p>
     * Given: Существует два события в базе данных
     * When: Выполняется запрос на получение всех событий
     * Then: Возвращается список из двух событий с кодом OK
     */
    @Test
    void shouldReturnAllEvents() {
        List<Event> events = List.of(createDummyEvent(1L), createDummyEvent(2L));
        given(eventRepository.findAll()).willReturn(events);

        ResponseEntity<List<Event>> response = restTemplate.exchange(
                "/api/events/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Event>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    /**
     * Scenario: Получение события по ID
     * <p>
     * Given: Существует событие с известным ID
     * When: Выполняется запрос на получение события по данному ID
     * Then: Возвращается данное событие с кодом OK
     */
    @Test
    void shouldReturnEventByValidId() {
        long validId = 1L;
        Event dummyEvent = createDummyEvent(validId);
        given(eventRepository.findById(validId)).willReturn(Optional.of(dummyEvent));

        ResponseEntity<Event> response = restTemplate.getForEntity("/api/events/" + validId, Event.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    /**
     * Scenario: Создание нового события
     * <p>
     * Given: Валидный объект события
     * When: Выполняется запрос на создание события
     * Then: Возвращается ответ с кодом CREATED и ссылка на созданный ресурс
     */
    @Test
    void shouldCreateEvent() {
        Event inputEvent = createDummyEvent(null);
        Event createdEvent = createDummyEvent(1L);
        given(eventService.createEvent(any())).willReturn(createdEvent);

        RequestEntity<Event> request = RequestEntity.post(URI.create("/api/events/"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(inputEvent);

        ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation().toString()).contains("/api/events/1");
        assertThat(createdEvent.getId()).isNotNull();
    }

    /**
     * Scenario: Обновление существующего события
     * <p>
     * Given: Событие существует в базе данных
     * When: Выполняется запрос на обновление события
     * Then: Обновленные данные сохраняются и возвращаются с кодом OK
     */
    @Test
    void shouldUpdateEvent() {
        long validId = 1L;
        Event updatedEvent = createDummyEvent(validId);
        given(eventRepository.findById(validId)).willReturn(Optional.of(updatedEvent));

        RequestEntity<Event> request = RequestEntity.put(URI.create("/api/events/" + validId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(updatedEvent);

        ResponseEntity<Event> response = restTemplate.exchange(request, Event.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedEvent);
    }

    /**
     * Scenario: Удаление события
     * <p>
     * Given: Событие существует в базе данных
     * When: Выполняется запрос на удаление события
     * Then: Событие удаляется, возвращается ответ с кодом NO_CONTENT
     */
    @Test
    void shouldDeleteEvent() {
        long validId = 1L;

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/events/" + validId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(eventRepository, times(1)).deleteById(validId);
    }

    private Event createDummyEvent(Long id) {
        Event event = new Event();
        event.setId(id);
        event.setMessage("Test Event");
        event.setOccurredAt(ZonedDateTime.now());
        return event;
    }
}