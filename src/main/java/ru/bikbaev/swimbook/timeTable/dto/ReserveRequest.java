package ru.bikbaev.swimbook.timeTable.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReserveRequest {
    private Long clientId;
    private LocalDateTime datetime;
}
