package ru.bikbaev.swimbook.timeTable.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PoolVisitSearchRequest {
    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 50, message = "от 2 до 50 символов")
    private String name;

    @NotNull(message = "Дата посещение не должно быть пустым")
    private LocalDate date;
}
