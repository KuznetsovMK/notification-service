package com.example.notification_service.service;

import com.example.notification_service.entity.Event;
import com.example.notification_service.repository.EventRepository;
import com.example.notification_service.web_socket.service.PrivateNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final PrivateNotificationService privateNotificationService;

    @Transactional
    public Event createEvent(Event event) {
        event.setOccurredAt(ZonedDateTime.now());
        Event saved = eventRepository.save(event);
        privateNotificationService.sendEvent(saved);
        return event;
    }
}
