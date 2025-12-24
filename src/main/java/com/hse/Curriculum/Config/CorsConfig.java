package com.hse.Curriculum.Config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {  // Cambié el nombre a CorsConfig (con C mayúscula)


    @Value("${cors.allowed.origins:http://localhost:3000,http://localhost:4200}")
    private String allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Orígenes permitidos (desarrollo + producción)
        List<String> origins = Arrays.asList(
                // Localhost para desarrollo
                "http://localhost:3000",
                "http://localhost:4200",
                "http://localhost:5173",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:4200",

                // Render (tu backend en Swagger)
                "https://curriculum-web-0aks.onrender.com",

                // AGREGA AQUÍ LA URL DEL FRONTEND DEL DESARROLLADOR
                // Por ejemplo: "https://frontend-app.vercel.app"
                // O si usa Render también: "https://su-frontend.onrender.com"

                // Patrón comodín para subdominios de Render (opcional)
                "https://*.onrender.com"
        );

        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "https://*.onrender.com",
                "https://*.vercel.app",
                "https://*.netlify.app"
        ));

        // Credenciales habilitadas
        config.setAllowCredentials(true);

        // Headers permitidos
        config.setAllowedHeaders(Arrays.asList("*"));

        // Métodos HTTP
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));

        // Headers expuestos
        config.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));

        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
