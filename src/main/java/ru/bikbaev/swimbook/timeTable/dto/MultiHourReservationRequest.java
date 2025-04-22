package ru.bikbaev.swimbook.timeTable.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
@Schema(description = "Request для бронирование нескольких часов в бассейне")
@Data
@Builder
@AllArgsConstructor
public class MultiHourReservationRequest {
    @Schema(
            description = "id клиента",
            example = "1"
    )
    @NotNull(message = "ID клиента не может быть пустым")
    private Long clientId;

    @Schema(
            description = "Дата бронирования (формат: yyyy-MM-dd)",
            example = "2025-04-21"
    )
    @NotNull(message = "Дата бронирования не может быть пустой")
    private LocalDate date;

    @Schema(
            description = "Время начала бронирования, время должно быть кратно часу, например 12:00 (формат: HH:mm)",
            example = "12:00"
    )
    @NotNull(message = "Время начала не может быть пустым")
    private LocalTime startTime;

    @Schema(
            description = "Продолжительность бронирования в часах (минимум 1 час)",
            example = "3"
    )
    @Min(value = 1, message = "Минимум 1 час")
    private int durationHours;
}
