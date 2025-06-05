package com.example.notification_service.mapper;

import com.example.notification_service.dto.UserDto;
import com.example.notification_service.entity.User;
import com.example.notification_service.enums.DayOfWeekEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    ObjectMapper objectMapper = new ObjectMapper();

    List<UserDto> toDto(List<User> users);

    @Mapping(
            target = "notificationSchedule",
            source = "notificationSchedule",
            qualifiedByName = "exportNotificationSchedule"
    )
    UserDto toDto(User user);


    @Named("exportNotificationSchedule")
    default String exportNotificationSchedule(String notificationSchedule) {
        if (notificationSchedule == null || notificationSchedule.isBlank()) {
            return null;
        }

        try {
            Map<String, List<String>> schedule = objectMapper.readValue(
                    notificationSchedule,
                    new TypeReference<>() {
                    });

            return buildNormalizedSchedule(schedule);
        } catch (IOException ex) {
            throw new RuntimeException("Ошибка парсинга расписания", ex);
        }
    }

    private String buildNormalizedSchedule(Map<String, List<String>> schedule) {
        StringBuilder result = new StringBuilder();

        DayOfWeekEnum lastStartDay = null;
        DayOfWeekEnum lastEndDay = null;
        String lastTimes = null;

        for (DayOfWeekEnum day : DayOfWeekEnum.dayOfWeeks()) {
            List<String> times = schedule.getOrDefault(day.name(), List.of());

            if (times.isEmpty()) continue;

            String currentTimes = formatTimes(times);

            if (!currentTimes.equals(lastTimes)) {
                if (lastStartDay != null) {
                    appendRange(result, lastStartDay, lastEndDay, lastTimes);
                }

                lastStartDay = day;
                lastEndDay = day;
                lastTimes = currentTimes;
            } else {
                lastEndDay = day;
            }
        }

        if (lastStartDay != null) {
            appendRange(result, lastStartDay, lastEndDay, lastTimes);
        }

        return result.toString().trim();
    }

    private void appendRange(StringBuilder builder, DayOfWeekEnum startDay, DayOfWeekEnum endDay, String times) {
        String range = startDay.getRuName();
        if (!startDay.equals(endDay)) {
            range += "-" + endDay.getRuName();
        }
        builder.append(builder.isEmpty() ? "" : ", ").append(range).append(times);
    }

    private String formatTimes(List<String> times) {
        return times.stream()
                .map(period -> {
                    String[] split = period.split("-");
                    return " с %s до %s".formatted(split[0], split[1]);
                })
                .collect(Collectors.joining(" и"));
    }
}
