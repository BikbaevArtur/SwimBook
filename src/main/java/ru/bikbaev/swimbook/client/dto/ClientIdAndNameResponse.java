package ru.bikbaev.swimbook.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "Response с id клиента и именем")
@Data
@AllArgsConstructor
public class ClientIdAndNameResponse {
    @Schema(description = "id клиента")
    private Long id;

    @Schema(description = "Имя")
    private String name;
}
