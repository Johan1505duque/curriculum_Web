package com.hse.Curriculum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class curriculumApplication {

    @Value("${server.port}")
    private String serverPort;

    @Value("${app.url:http://localhost}")
    private String appUrl;

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    public static void main(String[] args) {
        SpringApplication.run(curriculumApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // Construir la URL base según el entorno
        String baseUrl = appUrl.contains("localhost") || appUrl.contains("127.0.0.1")
                ? appUrl + ":" + serverPort
                : appUrl;

        // Formatear las líneas para que queden alineadas
        String environment = String.format("%-37s", "Entorno: " + activeProfile.toUpperCase());
        String swaggerUrl = String.format("%-37s", baseUrl + "/swagger-ui.html");
        String apiDocsUrl = String.format("%-37s", baseUrl + "/v3/api-docs");
        String healthUrl = String.format("%-37s", baseUrl + "/actuator/health");

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║     🚀 CURRICULUM API INICIADA CORRECTAMENTE     ║");
        System.out.println("╠════════════════════════════════════════════════════╣");
        System.out.println("║  " + environment + "║");
        System.out.println("║  Swagger UI:  " + swaggerUrl + "║");
        System.out.println("║  API Docs:    " + apiDocsUrl + "║");
        System.out.println("║  Health:      " + healthUrl + "║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
    }
}