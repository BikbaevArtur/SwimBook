package ru.bikbaev.swimbook.timeTable.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CancelRequest {
    private Long clientId;
    private UUID orderId;
}
