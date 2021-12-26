package com.github.skyg0d.skydrinksapi.property;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "my.websocket")
@Getter
@Setter
@ToString
public class WebSocketProperties {

    private long sendClientRequestUpdateDelay = 10000;

}
