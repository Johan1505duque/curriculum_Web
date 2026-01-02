package com.hse.Curriculum.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Orígenes específicos permitidos
        config.setAllowedOrigins(Arrays.asList(
                // Localhost para desarrollo
                "http://localhost:8080",
                "http://localhost:3000",
                "http://localhost:4200",
                "http://localhost:5173",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:4200",

                // IP de tu compañero (ajusta el puerto si es necesario)
                "http://192.168.20.63",
                "http://192.168.20.63:3000",
                "http://192.168.20.63:4200",
                "http://192.168.20.63:5173",
                "http://192.168.20.63:8080",

                // Producción
                "https://curriculum-web-0aks.onrender.com",
                "https://ips-heart-s.vercel.app"
        ));

        // Credenciales habilitadas
        config.setAllowCredentials(true);

        // Headers permitidos
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Origin"
        ));

        // Métodos HTTP
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Headers expuestos
        config.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count"
        ));

        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}