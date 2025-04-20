package ru.bikbaev.swimbook.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class WorkTimeDto {
    private LocalTime startTime;
    private LocalTime endTime;
}
