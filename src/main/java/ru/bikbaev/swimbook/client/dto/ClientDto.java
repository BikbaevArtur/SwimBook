package ru.bikbaev.swimbook.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Клиент")

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientDto {

    private Long id;

    @Schema(description = "Имя клиента", example = "Иван")
    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 50, message = "от 2 до 50 символов")
    private String name;

    @Schema(description = "номер телефона", example = "+77777777777")
    @NotBlank(message = "Телефон не должен быть пустым")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Телефон должен содержать только цифры, начинаться с +, min = 10  max = 15)"
    )
    private String phone;

    @Schema(description = "email", example = "ivan@mail.ru")
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;
}
