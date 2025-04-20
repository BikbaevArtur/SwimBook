package ru.bikbaev.swimbook.timeTable.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class TimeRecord {
    private LocalTime time;
    private int count;
}
