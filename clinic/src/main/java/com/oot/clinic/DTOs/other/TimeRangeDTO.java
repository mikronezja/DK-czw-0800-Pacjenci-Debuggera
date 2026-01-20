package com.oot.clinic.DTOs.other;

import java.time.LocalTime;

public record TimeRangeDTO(
        LocalTime startTime,
        LocalTime endTime
) {
}
