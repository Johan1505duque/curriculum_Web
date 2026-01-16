package com.hse.Curriculum.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @Value("${app.ngrok.url:}")
    private String ngrokUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        List<Server> servers = new ArrayList<>();

        // Si estamos en LOCAL
        if ("local".equals(activeProfile)) {

            // 1.LOCALHOST -
            if (ngrokUrl != null && !ngrokUrl.isEmpty()) {
                // 1️⃣ LOCALHOST → Swagger SIEMPRE aquí
                servers.add(new Server()
                        .url("http://localhost:8080")
                        .description("💻 Local (Swagger)")
                );
            }

            // 2. NGROK
            if (ngrokUrl != null && !ngrokUrl.isEmpty()) {
                servers.add(new Server()
                        .url(ngrokUrl)
                        .description("🌐 Ngrok (Frontend / externo)")
                );

            // 3. RENDER - Para probar producción
            servers.add(new Server()
                    .url("https://curriculum-web-0aks.onrender.com")
                        .description("🚀 Producción (Render)"));
            }
        }
        // En PRODUCCIÓN
        else {
            // PRODUCCIÓN
            servers.add(new Server()
                    .url("https://curriculum-web-0aks.onrender.com")
                    .description("🚀 Producción (Render)")
            );
        }
        // 🔐 DEFINICIÓN DE JWT
        Components components = new Components()
                .addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                );

        return new OpenAPI()
                .info(new Info()
                        .title("Curriculum API")
                        .version("1.0.0")
                        .description("API REST para gestión de usuarios y currículums")
                        .contact(new Contact()
                                .name("Equipo Curriculum")
                                .email("contacto@curriculum.com")))
                .servers(servers)
                .components(components);

    }
}
