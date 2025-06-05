package com.example.notification_service.web_socket.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventMessage implements WSMessage {
    private String message;
}
