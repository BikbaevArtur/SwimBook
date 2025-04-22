package ru.bikbaev.swimbook.timeTable.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Schema(description = "Request для отмены бронирования")
@Data
@AllArgsConstructor
public class CancelRequest {

    @Schema(description = "id клиента", example = "1")
    @NotNull(message = "id клиента не может быть пустым")
    private Long clientId;

    @Schema(description = "id заказа который нужно отменить")
    @NotNull(message = "id записи не может быть пустым")
    private UUID orderId;
}
