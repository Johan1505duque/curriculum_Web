package com.curriculum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class curriculumApplication {

    public static void main(String[] args) {
        SpringApplication.run(curriculumApplication.class, args);

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║     🚀 CURRICULUM API INICIADA CORRECTAMENTE     ║");
        System.out.println("╠════════════════════════════════════════════════════╣");
        System.out.println("║  Swagger UI:  http://localhost:8080/swagger-ui.html");
        System.out.println("║  API Docs:    http://localhost:8080/v3/api-docs   ");
        System.out.println("║  Health:      http://localhost:8080/api/users/health");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
    }
}