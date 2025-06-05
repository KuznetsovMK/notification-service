package com.example.notification_service.entity;

import com.example.notification_service.configuration.NotificationScheduleDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "users", schema = "notification_service")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue()
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @JsonDeserialize(using = NotificationScheduleDeserializer.class)
    @Column(name = "notification_schedule")
    private String notificationSchedule;
}
