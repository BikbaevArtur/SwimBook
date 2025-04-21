package ru.bikbaev.swimbook.timeTable.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReserveRequest {
    @NotNull(message = "id клиента не может быть пустым")
    private Long clientId;
    @NotNull(message = "дата записи не может быть пустым")
    private LocalDateTime datetime;
}
