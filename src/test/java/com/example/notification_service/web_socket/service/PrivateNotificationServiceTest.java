package com.example.notification_service.web_socket.service;

import com.example.notification_service.entity.Event;
import com.example.notification_service.entity.User;
import com.example.notification_service.entity.UserEvent;
import com.example.notification_service.repository.EventRepository;
import com.example.notification_service.repository.UserEventRepository;
import com.example.notification_service.service.UserService;
import com.example.notification_service.web_socket.message.WSMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class PrivateNotificationServiceTest {

    @InjectMocks
    private PrivateNotificationService serviceUnderTest;

    @Mock
    private SimpMessagingTemplate template;

    @Mock
    private UserService userService;

    @Mock
    private UserEventRepository userEventRepository;

    @Mock
    private EventRepository eventRepository;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Scenario: Отправка события активным пользователям
     * <p>
     * Given: Пользователь активен и находится онлайн
     * When: Выполнен метод sendEvent()
     * Then: Сообщение доставляется пользователю через веб-сокеты
     */
    @Test
    void shouldSendEventToActiveUser() {
        User activeUser = new User();
        activeUser.setId(1L);
        Event event = new Event();
        event.setMessage("Test Event");

        given(userService.findAll()).willReturn(List.of(activeUser));
        given(userService.isUserActive(any(), any())).willReturn(true);

        serviceUnderTest.sendEvent(event);

        verify(template, times(1))
                .convertAndSend(eq("/queue/user/" + activeUser.getId()), any(WSMessage.class));
    }

    /**
     * Scenario: Добавление события в очередь неактивному пользователю
     * <p>
     * Given: Пользователь не активен
     * When: Выполнен метод sendEvent()
     * Then: Событие помещается в очередь
     */
    @Test
    void shouldAddEventToInactiveUserQueue() {
        User inactiveUser = new User();
        Event event = new Event();
        event.setMessage("Test Event");
        given(userService.findAll()).willReturn(List.of(inactiveUser));
        given(userService.isUserActive(inactiveUser, LocalDateTime.now())).willReturn(false);

        serviceUnderTest.sendEvent(event);

        verify(userEventRepository, times(1))
                .save(any(UserEvent.class));
    }

    /**
     * Scenario: Обработка событий пользователя
     * <p>
     * Given: Пользователь имеет несколько отложенных событий
     * When: Выполнен метод processUserEvents()
     * Then: События доставлены пользователю и очищены из очереди
     */
    @Test
    void shouldProcessQueuedEvents() {
        User user = new User();
        Event event = new Event();
        event.setMessage("Test Event");
        UserEvent queuedEvent = new UserEvent(user, event);
        given(userEventRepository.findByUserId(user.getId())).willReturn(List.of(queuedEvent));
        given(eventRepository.findById(event.getId())).willReturn(Optional.of(event));

        serviceUnderTest.processUserEvents(user);

        verify(template, times(1))
                .convertAndSend(eq("/queue/user/" + user.getId()), any(WSMessage.class));
        verify(userEventRepository, times(1)).delete(queuedEvent);
    }

    /**
     * Scenario: Логирование и доставка сообщения
     * <p>
     * Given: Пользователь и событие
     * When: Выполнен метод sendAndLogEventMessage()
     * Then: Сообщение отправляется пользователю и регистрируется в логах
     */
    @Test
    void shouldSendAndLogEventMessage() {
        User user = new User();
        Event event = new Event();
        event.setMessage("Test Event");

        serviceUnderTest.sendAndLogEventMessage(user, event);

        verify(template, times(1))
                .convertAndSend(eq("/queue/user/" + user.getId()), any(WSMessage.class));
    }
}