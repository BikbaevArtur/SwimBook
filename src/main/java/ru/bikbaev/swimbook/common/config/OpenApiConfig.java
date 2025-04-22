package ru.bikbaev.swimbook.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Swim book",
                description = "REST API для организации работы бассейна (регистрация клиентов, работа с записями на посещение)",
                version = "${v.0}",
                contact = @Contact(
                        email = "bigbyar4i@icloud.com",
                        name = "Artur Bikbaev"
                )
        )
)
public class OpenApiConfig {
}
