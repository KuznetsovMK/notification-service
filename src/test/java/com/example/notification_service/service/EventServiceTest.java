package com.example.notification_service.service;


import com.example.notification_service.entity.Event;
import com.example.notification_service.repository.EventRepository;
import com.example.notification_service.web_socket.service.PrivateNotificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class EventServiceTest {

    @InjectMocks
    private EventService serviceUnderTest;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PrivateNotificationService privateNotificationService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Scenario: Создание нового события
     * <p>
     * Given: Валидный объект события
     * When: Выполняется метод createEvent
     * Then: Объект сохраняется в репозиторий, уведомление отправляется приватному сервису
     */
    @Test
    void shouldCreateEventSuccessfully() {
        Event inputEvent = new Event();
        inputEvent.setMessage("Sample Event");

        Event result = serviceUnderTest.createEvent(inputEvent);

        verify(eventRepository, times(1)).save(any());
        verify(privateNotificationService, times(1)).sendEvent(any());

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getOccurredAt());
    }

    /**
     * Scenario: Проверка установки текущего времени
     * <p>
     * Given: Новый объект события
     * When: Выполняется метод createEvent
     * Then: Временная отметка установлена на текущее время
     */
    @Test
    void shouldSetCurrentTimeOnCreation() {
        Event inputEvent = new Event();
        inputEvent.setMessage("Sample Event");

        Event result = serviceUnderTest.createEvent(inputEvent);

        Assertions.assertNotNull(result.getOccurredAt());
    }

    /**
     * Scenario: Генерация уведомления
     * <p>
     * Given: Новое событие создано
     * When: Метод createEvent вызван
     * Then: Приватный сервис уведомлений получает событие
     */
    @Test
    void shouldNotifyAboutCreatedEvent() {
        Event inputEvent = new Event();
        inputEvent.setMessage("Sample Event");

        serviceUnderTest.createEvent(inputEvent);

        verify(privateNotificationService, times(1)).sendEvent(any());
    }
}