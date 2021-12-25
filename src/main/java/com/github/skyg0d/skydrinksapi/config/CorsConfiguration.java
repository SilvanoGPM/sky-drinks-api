package com.github.skyg0d.skydrinksapi.config;

import com.github.skyg0d.skydrinksapi.property.CorsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class CorsConfiguration {

    private final CorsProperties corsProperties;

    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/**")
                        .allowedOrigins(corsProperties.getOrigins().toArray(new String[0]))
                        .allowCredentials(true)
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE");
            }
        };

    }

}
