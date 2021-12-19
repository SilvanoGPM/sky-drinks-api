package com.github.skyg0d.skydrinksapi.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "my.cors")
public class CorsProperties {

    private List<String> origins = List.of("*");

}
