package ru.bikbaev.swimbook.timeTable.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CancelRequest {

    @NotNull(message = "id клиента не может быть пустым")
    private Long clientId;

    @NotNull(message = "id записи не может быть пустым")
    private UUID orderId;
}
