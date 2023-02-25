package ru.vsu.csf.asashina.marketserver.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "Bearer")
@OpenAPIDefinition(
        info = @Info(
                title = "Market Server API",
                description = "Описание всех эндпоинтов сервера \"маркет\"",
                contact = @Contact(name = "Анастасия Сашина", email = "sashina@cs.vsu.ru"),
                version = "1.0.2"),
        security = { @SecurityRequirement(name = "Bearer") })
public class SpringdocConfiguration {
}
