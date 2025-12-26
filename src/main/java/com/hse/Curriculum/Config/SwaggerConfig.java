package com.hse.Curriculum.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @Bean
    public OpenAPI customOpenAPI() {
        List<Server> servers = new ArrayList<>();

        // ✅ Si estamos en LOCAL, poner localhost PRIMERO
        if ("local".equals(activeProfile)) {
            servers.add(new Server()
                    .url("http://localhost:8080")
                    .description("Servidor Local de Desarrollo"));

            servers.add(new Server()
                    .url("https://curriculum-web-0aks.onrender.com")
                    .description("Servidor de Producción (Render)"));
        }
        // En PRODUCCIÓN, poner Render PRIMERO
        else {
            servers.add(new Server()
                    .url("https://curriculum-web-0aks.onrender.com")
                    .description("Servidor de Producción (Render)"));

            servers.add(new Server()
                    .url("http://localhost:8080")
                    .description("Servidor Local de Desarrollo"));
        }

        return new OpenAPI()
                .info(new Info()
                        .title("Curriculum API")
                        .version("1.0.0")
                        .description("API REST para gestión de usuarios y currículums")
                        .contact(new Contact()
                                .name("Equipo Curriculum")
                                .email("contacto@curriculum.com")))
                .servers(servers);
    }
}
