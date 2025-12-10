package com.curriculum;

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

    public static void main(String[] args) {
        SpringApplication.run(curriculumApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        String baseUrl = appUrl.contains("localhost")
                ? appUrl + ":" + serverPort
                : appUrl;

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║     🚀 CURRICULUM API INICIADA CORRECTAMENTE     ║");
        System.out.println("╠════════════════════════════════════════════════════╣");
        System.out.println("║  Swagger UI:  " + baseUrl + "/swagger-ui.html");
        System.out.println("║  API Docs:    " + baseUrl + "/v3/api-docs");
        System.out.println("║  Health:      " + baseUrl + "/actuator/health");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
    }
}