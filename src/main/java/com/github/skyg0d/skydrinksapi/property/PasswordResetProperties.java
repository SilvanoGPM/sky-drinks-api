package com.github.skyg0d.skydrinksapi.property;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "password.reset")
@Getter
@Setter
@ToString
public class PasswordResetProperties {

    private int expireMinutes = 30;

    private int tokenLength = 10;

}
