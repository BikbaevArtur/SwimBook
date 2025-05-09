package ru.bikbaev.swimbook.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Schema(description = "Request для создание нового клиента")
@Data

public class ClientRequest {

    @Schema(description = "имя",example = "Иван")
    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 50, message = "от 2 до 50 символов")
    private String name;

    @Schema(description = "Номер телефона(код страны обязателен, например +7)",example = "+77777777777")
    @NotBlank(message = "Телефон не должен быть пустым")
    @Size(min = 10,max = 15,message = "Длина телефона должна быть от 10 до 15 символов")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Телефон должен содержать только цифры, начинаться с +, min = 10  max = 15)"
    )
    private String phone;

    @Schema(description = "email",example = "ivan@mail.ru")
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;
}