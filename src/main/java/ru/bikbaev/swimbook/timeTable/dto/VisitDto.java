package ru.bikbaev.swimbook.timeTable.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class VisitDto {
    private UUID orderId;
    private LocalTime time;
}
