package ru.bikbaev.swimbook.timeTable.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
public class MultiHourReservationRequest {
    private Long clientId;
    private LocalDate date;
    private LocalTime startTime;
    private int durationHours;
}
