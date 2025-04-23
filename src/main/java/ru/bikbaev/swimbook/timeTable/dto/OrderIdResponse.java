package ru.bikbaev.swimbook.timeTable.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description ="Ответ на создание новой записи" )
@Data
@AllArgsConstructor
public class OrderIdResponse {
    @Schema(description = "id новой записи в бронировании")
    private String orderId;
}
