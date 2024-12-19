package dev.theezzfix.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:5173",           // Local development
                    "https://theezfixapi.onrender.com", // Backend URL
                    "https://theezzfix.onrender.com"    // Frontend URL
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Explicitly allow HTTP methods
                .allowedHeaders("Content-Type", "Authorization", "X-Requested-With", "Accept") // Avoid wildcard
                .allowCredentials(true);           // Allow credentials
    }
}
