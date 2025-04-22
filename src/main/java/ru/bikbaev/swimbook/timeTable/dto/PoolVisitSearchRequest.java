package ru.bikbaev.swimbook.timeTable.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "Request для поиска посещения бассейна по имени клиента и дате")
@Data
@AllArgsConstructor
public class PoolVisitSearchRequest {

    @Schema(description = "Имя клиента",
            example = "Иван",
            minLength = 2,
            maxLength = 50)
    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 50, message = "от 2 до 50 символов")
    private String name;


    @Schema(
            description = "Дата посещения",
            example = "2025-04-21"
    )
    @NotNull(message = "Дата посещение не должно быть пустым")
    private LocalDate date;
}
