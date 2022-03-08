package com.github.skyg0d.skydrinksapi.property;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "my.files")
public class FileStorageProperties {

    private String uploadDir = "./uploaded-files";

    private String drinksDir = "drinks";

    private String usersDir = "users";

}
