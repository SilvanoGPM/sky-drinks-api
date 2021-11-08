package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.exception.CustomFileNotFoundException;
import com.github.skyg0d.skydrinksapi.exception.FileStorageException;
import com.github.skyg0d.skydrinksapi.property.FileStorageProperties;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FileStorageService {

    private final FileStorageProperties fileStorageProperties;
    private final Path fileStoragePath;
    private final Path imagesPath;
    private final String projectDir;

    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;

        this.fileStoragePath = Paths
                .get(fileStorageProperties.getUploadDir())
                .toAbsolutePath()
                .normalize();

        this.imagesPath = fileStoragePath.resolve(fileStorageProperties.getImagesDir());

        this.projectDir = Paths.get("")
                .toAbsolutePath()
                .resolve(fileStoragePath)
                .toString();

        createStorageDir();
    }

    public Page<String> listFiles(Pageable pageable) {
        List<String> files = listFiles();
        return new PageImpl<>(files, pageable, files.size());
    }

    public List<String> listFiles() {
        try {
            return Files.walk(this.fileStoragePath)
                    .filter(Files::isRegularFile)
                    .map(path -> path.toAbsolutePath().toString().replace(projectDir, ""))
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new FileStorageException("Aconteceu um erro ao tentar listar os arquivos.");
        }
    }

    public String storageImage(MultipartFile file) {
        List<String> contentTypes = List.of(
                MediaType.IMAGE_JPEG_VALUE,
                MediaType.IMAGE_PNG_VALUE
        );

        if (!contentTypes.contains(file.getContentType())) {
            throw new FileStorageException("Arquivo não é uma imagem.");
        }

        return storeFile(file, imagesPath);
    }

    public String storeFile(MultipartFile file) {
        return storeFile(file, fileStoragePath);
    }

    public byte[] getImage(String fileName) {
        try {
            InputStream inputStream = loadFileAsResource(fileName, imagesPath).getInputStream();
            return IOUtils.toByteArray(inputStream);
        } catch (IOException ex) {
            throw new FileStorageException("Aconteceu um erro ao tentar carregar a imagem.", ex);
        }
    }

    public Resource loadFileAsResource(String fileName, Path path) {
        String fileNotFoundMessage = "Arquivo não encontrado: " + fileName;

        try {
            Path filePath = path.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new CustomFileNotFoundException(fileNotFoundMessage);
            }

        } catch (MalformedURLException ex) {
            throw new CustomFileNotFoundException(fileNotFoundMessage, ex);
        }

    }

    public void deleteImage(String fileName) {
        deleteFile(fileName, imagesPath);
    }

    public void deleteFile(String fileName, Path path) {
        Path filePath = path.resolve(fileName).normalize();
        String fileNotFoundMessage = "Arquivo não encontrado: " + fileName;

        try {
            boolean deleted = Files.deleteIfExists(filePath);

            if (!deleted) {
                throw new CustomFileNotFoundException(fileNotFoundMessage);
            }
        } catch (IOException ex) {
            throw new CustomFileNotFoundException(fileNotFoundMessage, ex);
        }
    }

    private String storeFile(MultipartFile file, Path path) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {

            if (fileName.contains("..")) {
                throw new FileStorageException("Desculpa, mas o nome do arquivo possuí sequências de caminho inválidas: " + fileName);
            }

            Path targetLocation = getTargetLocation(path, fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return targetLocation.getFileName().toString();
        } catch (IOException ex) {
            String message = String.format("Não foi possível persistir o arquivo %s. Por favor, tente novamente.", fileName);
            throw new FileStorageException(message, ex);
        }
    }

    private Path getTargetLocation(Path path, String fileName) {
        int files = 1;

        // Increments one in "files" while find fileName.
        while (Files.exists(path.resolve(fileName))) {
            String[] fileParts = fileName.split("\\.");
            fileName = String.format("%s_%d.%s", fileParts[0].replaceAll("_\\d", ""), files, fileParts[1]);
            files++;
        }

        return path.resolve(fileName);
    }

    private void createStorageDir() {
        Path imagesPath = fileStoragePath.resolve(fileStorageProperties.getImagesDir());

        List.of(fileStoragePath, imagesPath).forEach((path) -> {
            if (!Files.exists(path)) {
                try {
                    Files.createDirectory(path);
                } catch (IOException ex) {
                    throw new FileStorageException("Não foi possível criar o diretório: " + this.fileStoragePath, ex);
                }
            }
        });
    }

}
