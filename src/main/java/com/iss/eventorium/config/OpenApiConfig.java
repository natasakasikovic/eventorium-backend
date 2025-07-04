package com.iss.eventorium.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Eventorium API",
                description = "OpenApi documentation for Eventorium application",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8080"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT"
)
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                Schema<?> errorSchemaRef = new Schema<>().$ref("#/components/schemas/ExceptionResponse");

                ApiResponse unauthorizedResponse = new ApiResponse()
                        .description("Unauthorized")
                        .content(new Content().addMediaType("application/json",
                                new MediaType()
                                        .schema(errorSchemaRef)
                                        .addExamples("UnauthorizedExample",
                                                new Example().value("{\"error\": \"Unauthorized\", \"message\": \"Full authentication is required to access this resource\"}")
                                        )
                        ));

                ApiResponse forbiddenResponse = new ApiResponse()
                        .description("Forbidden")
                        .content(new Content().addMediaType("application/json",
                                new MediaType()
                                        .schema(errorSchemaRef)
                                        .addExamples("ForbiddenExample",
                                                new Example().value("{\"error\": \"Forbidden\", \"message\": \"You do not have permission to access this resource\"}")
                                        )
                        ));

                return new OpenAPI()
                        .components(new Components()
                                .addSchemas("ExceptionResponse", new Schema<>()
                                        .type("object")
                                        .addProperty("error", new Schema<>().type("string"))
                                        .addProperty("message", new Schema<>().type("string")))
                                .addResponses("UnauthorizedResponse", unauthorizedResponse)
                                .addResponses("ForbiddenResponse", forbiddenResponse));
        }
}
