package com.example.notification_service.web_socket.service;


import com.example.notification_service.entity.Event;
import com.example.notification_service.entity.User;
import com.example.notification_service.entity.UserEvent;
import com.example.notification_service.repository.EventRepository;
import com.example.notification_service.repository.UserEventRepository;
import com.example.notification_service.service.UserService;
import com.example.notification_service.web_socket.message.EventMessage;
import com.example.notification_service.web_socket.message.WSMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivateNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(PrivateNotificationService.class);

    private final SimpMessagingTemplate template;
    private final UserService userService;
    private final UserEventRepository userEventRepository;
    private final EventRepository eventRepository;

    public void sendEvent(Event event) {
        List<User> users = userService.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (User user : users) {
            if (userService.isUserActive(user, now))
                sendAndLogEventMessage(user, event);
            else
                addEventToQueue(user, event);
        }
    }

    private void addEventToQueue(User user, Event event) {
        UserEvent userEvent = new UserEvent(user, event);
        userEventRepository.save(userEvent);
    }

    public void processUserEvents(User user) {
        List<UserEvent> userEvents = userEventRepository.findByUserId(user.getId());
        userEvents.forEach(userEvent -> {
            Event event = eventRepository.findById(userEvent.getEvent().getId()).orElseThrow();
            sendAndLogEventMessage(user, event);
            userEventRepository.delete(userEvent);
        });
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void sendAndLogEventMessage(User user, Event event) {
        sendEventMessage(user.getId(), new EventMessage(event.getMessage()));
        logger.info("{} Пользователю {} отправлено оповещение с текстом: {}",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                user.getFullName(),
                event.getMessage());
    }

    private void sendEventMessage(Long id, WSMessage message) {
        template.convertAndSend("/queue/user/" + id, message);
    }
}
