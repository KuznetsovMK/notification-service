package com.example.notification_service.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "user_events", schema = "notification_service")
@NoArgsConstructor
public class UserEvent {
    @EmbeddedId
    private UserEventPK id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("eventId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    public UserEvent(User user, Event event) {
        this.id = new UserEventPK(user.getId(), event.getId());
        this.user = user;
        this.event = event;
    }
}
