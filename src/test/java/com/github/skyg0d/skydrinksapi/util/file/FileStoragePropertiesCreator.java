package com.github.skyg0d.skydrinksapi.util.file;

import com.github.skyg0d.skydrinksapi.property.FileStorageProperties;
import lombok.SneakyThrows;

import java.nio.file.Files;

public class FileStoragePropertiesCreator {

    public static FileStorageProperties createFileStorageProperties() {
        return FileStorageProperties
                .builder()
                .uploadDir(createTempDir())
                .drinksDir("drinks")
                .usersDir("user")
                .build();
    }

    @SneakyThrows
    private static String createTempDir() {
        return Files.createTempDirectory("sky_drinks_tests").toAbsolutePath().toString();
    }

}
