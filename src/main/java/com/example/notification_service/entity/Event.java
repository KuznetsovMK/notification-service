package com.example.notification_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "events", schema = "notification_service")
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue()
    private Long id;

    @Column
    private String message;

    @Column(name = "occurred_at")
    private ZonedDateTime occurredAt;
}
