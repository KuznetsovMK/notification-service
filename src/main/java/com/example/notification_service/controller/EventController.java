package com.example.notification_service.controller;

import com.example.notification_service.entity.Event;
import com.example.notification_service.repository.EventRepository;
import com.example.notification_service.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventRepository eventRepository;

    @GetMapping("/")
    public ResponseEntity<List<Event>> listEvents() {
        List<Event> events = eventRepository.findAll();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Optional<Event> eventOptional = eventRepository.findById(id);
        return eventOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<Void> createEvent(@Valid @RequestBody Event event) {
        Event savedEvent = eventService.createEvent(event);
        URI location = URI.create("/api/events/" + savedEvent.getId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @Valid @RequestBody Event updatedEvent) {
        Optional<Event> existingEventOpt = eventRepository.findById(id);
        if (existingEventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event existingEvent = existingEventOpt.get();
        existingEvent.setMessage(updatedEvent.getMessage());
        existingEvent.setOccurredAt(updatedEvent.getOccurredAt());
        eventRepository.save(existingEvent);
        return ResponseEntity.ok(existingEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
