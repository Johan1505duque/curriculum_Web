package com.curriculum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class curriculumApplication {

    public static void main(String[] args) {
        SpringApplication.run(curriculumApplication.class, args);
    }

    /**
     * Este método se ejecuta DESPUÉS de que la aplicación esté completamente iniciada
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║     🚀 CURRICULUM API INICIADA CORRECTAMENTE     ║");
        System.out.println("╠════════════════════════════════════════════════════╣");
        System.out.println("║  Swagger UI:  http://localhost:8080/swagger-ui.html");
        System.out.println("║  API Docs:    http://localhost:8080/v3/api-docs   ");
        System.out.println("║  Health:      http://localhost:8080/actuator/health");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
    }
}