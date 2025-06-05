package com.example.notification_service.service;

import com.example.notification_service.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ScheduleHelper {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void normalizeUserSchedule(User user) {
        try {
            Map<String, List<String>> schedule = objectMapper.readValue(
                    user.getNotificationSchedule(),
                    new ObjectMapper().getTypeFactory()
                            .constructMapLikeType(Map.class, String.class, List.class)
            );

            Map<DayOfWeek, List<String>> normalizedSchedule = new HashMap<>(schedule.size());
            for (var entry : schedule.entrySet()) {
                DayOfWeek upperCaseDay = DayOfWeek.valueOf(entry.getKey().toUpperCase());
                normalizedSchedule.put(upperCaseDay, entry.getValue());
            }

            user.setNotificationSchedule(objectMapper.writeValueAsString(normalizedSchedule));
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка при нормализации расписания", e);
        }
    }


    public boolean isUserActive(User user, LocalDateTime currentDateTime) {
        DayOfWeek currentDay = currentDateTime.getDayOfWeek();
        LocalTime currentTime = currentDateTime.toLocalTime();

        try {
            Map<DayOfWeek, List<String>> schedule = objectMapper.readValue(
                    user.getNotificationSchedule(),
                    new ObjectMapper().getTypeFactory()
                            .constructMapLikeType(Map.class, DayOfWeek.class, List.class)
            );

            if (schedule == null || schedule.isEmpty()) {
                return false;
            }

            List<String> intervals = schedule.getOrDefault(currentDay, List.of());
            return intervals.stream()
                    .anyMatch(interval -> isWithinInterval(interval, currentTime));
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка при проверке активности пользователя", e);
        }
    }

    private boolean isWithinInterval(String intervalStr, LocalTime currentTime) {
        String[] parts = intervalStr.split("-");
        LocalTime startTime = LocalTime.parse(parts[0].trim());
        LocalTime endTime = LocalTime.parse(parts[1].trim());
        return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
    }
}
