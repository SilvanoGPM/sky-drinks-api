package com.github.skyg0d.skydrinksapi.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "my.files")
public class FileStorageProperties {

    private String uploadDir = "./uploaded-files";

    private String imagesDir = "images";

}
