package ru.bikbaev.swimbook.timeTable.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Request для создание резерва бассейна")
@Data
@AllArgsConstructor
public class ReserveRequest {
    @Schema(description = "id клиента", example = "1")
    @NotNull(message = "id клиента не может быть пустым")
    private Long clientId;

    @Schema(
            description = "Дата и время бронирования, время должно быть кратно часу, например 14:00 (формат: yyyy-MM-dd'T'HH:mm)",
            example = "2025-04-21T14:00"
    )
    @NotNull(message = "дата записи не может быть пустым")
    private LocalDateTime datetime;
}
