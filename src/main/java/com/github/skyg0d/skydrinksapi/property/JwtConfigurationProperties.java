package com.github.skyg0d.skydrinksapi.property;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt.config")
@Getter
@Setter
@ToString
public class JwtConfigurationProperties {

    private String loginUrl = "/login/**";

    private String privateKey = "5fr9ZwnObBDO9eObLrADJrhUfcHyGtYB";

    @NestedConfigurationProperty
    private Header header = new Header();

    private int expiration = 60 * 60; // One hour in seconds

    @Getter
    @Setter
    public static class Header {

        private String name = "Authorization";
        private String prefix = "Bearer ";

    }

}
