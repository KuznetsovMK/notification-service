package com.example.notification_service.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum DayOfWeekEnum {
    MONDAY("понедельник"),
    TUESDAY("вторник"),
    WEDNESDAY("среда"),
    THURSDAY("четверг"),
    FRIDAY("пятница"),
    SATURDAY("суббота"),
    SUNDAY("воскресенье");

    private final String ruName;

    DayOfWeekEnum(String ruName) {
        this.ruName = ruName;
    }

    public static List<DayOfWeekEnum> dayOfWeeks() {
        return List.of(
                MONDAY, TUESDAY, WEDNESDAY,
                THURSDAY, FRIDAY, SATURDAY, SUNDAY
        );
    }
}