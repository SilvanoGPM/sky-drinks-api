package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.exception.CustomFileNotFoundException;
import com.github.skyg0d.skydrinksapi.exception.FileStorageException;
import com.github.skyg0d.skydrinksapi.property.FileStorageProperties;
import com.github.skyg0d.skydrinksapi.util.RolesUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class FileStorageService {

    public static final List<String> IMAGES_TYPES = List.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE
    );

    private final FileStorageProperties fileStorageProperties;
    private final Path fileStoragePath;
    private final Path drinksPath;
    private final Path usersPath;
    private final String projectDir;

    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;

        this.fileStoragePath = Paths
                .get(fileStorageProperties.getUploadDir())
                .toAbsolutePath()
                .normalize();

        this.drinksPath = fileStoragePath.resolve(fileStorageProperties.getDrinksDir());
        this.usersPath = fileStoragePath.resolve(fileStorageProperties.getUsersDir());


        this.projectDir = Paths.get("")
                .toAbsolutePath()
                .resolve(fileStoragePath)
                .toString();

        createStorageDir();
    }

    public Page<String> listFiles(Pageable pageable) {
        List<String> files = listFiles();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), files.size());

        List<String> content = start < files.size() ? files.subList(start, end) : Collections.emptyList();

        log.info("Retornando todos os arquivos com os parametros \"{}\"", pageable);

        return new PageImpl<>(content, pageable, files.size());
    }

    public List<String> listFiles() {
        try {
            log.info("Retornando todos os arquivos");

            return Files.walk(this.fileStoragePath)
                    .filter(Files::isRegularFile)
                    .map(path -> path.toAbsolutePath().toString()
                            .replace(projectDir, "")
                            .replace("\\", "/"))
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new FileStorageException("Aconteceu um erro ao tentar listar os arquivos.");
        }
    }

    public String storeProfilePicture(MultipartFile file, ApplicationUser user) {
        String fileName = user.getUuid().toString() + ".png";

        return storeFile(file, usersPath, fileName);
    }

    public String storeDrinkImage(MultipartFile file) {
        verifyIfIsAnImage(file);
        return storeFile(file, drinksPath);
    }

    public Map<String, MultipartFile> storeImages(List<MultipartFile> files) {
        return files
                .stream()
                .collect(Collectors.toMap(this::storeDrinkImage, (file) -> file));
    }

    public String storeFile(MultipartFile file) {
        return storeFile(file, fileStoragePath);
    }

    public Map<String, MultipartFile> storeFiles(List<MultipartFile> files) {
        return files
                .stream()
                .collect(Collectors.toMap(this::storeFile, (file) -> file));
    }

    public byte[] getDrinkImage(String fileName) {
        return getImage(fileName, drinksPath);
    }

    public byte[] getUserImage(String fileName) {
        return getImage(fileName, usersPath);
    }

    public byte[] getImage(String fileName, Path path) {
        try {
            InputStream inputStream = loadFileAsResource(fileName, path).getInputStream();
            return IOUtils.toByteArray(inputStream);
        } catch (IOException ex) {
            throw new FileStorageException("Aconteceu um erro ao tentar carregar a imagem.", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        return loadFileAsResource(fileName, fileStoragePath);
    }

    public void deleteDrinkImage(String fileName) {
        deleteFile(fileName, drinksPath);
    }

    public void deleteUserImage(String fileName, ApplicationUser user) {
        RolesUtil.verifyIfUserHasPermission(UUID.fromString(fileName.replace(".png", "")), user);
        deleteFile(fileName, usersPath);
    }

    public void deleteFile(String fileName) {
        deleteFile(fileName, fileStoragePath);
    }

    private void deleteFile(String fileName, Path path) {
        Path filePath = path.resolve(fileName).normalize();
        String fileNotFoundMessage = "Arquivo não encontrado: " + fileName;

        log.info("Tentando deletar arquivo com nome \"{}\"", fileName);

        try {
            boolean deleted = Files.deleteIfExists(filePath);

            if (!deleted) {
                throw new CustomFileNotFoundException(fileNotFoundMessage);
            }

            log.info("Arquivo \"{}\" deletado com sucesso!", fileName);
        } catch (IOException ex) {
            throw new CustomFileNotFoundException(fileNotFoundMessage, ex);
        }
    }

    private Resource loadFileAsResource(String fileName, Path path) {
        log.info("Pesquisando arquivo pelo nome \"{}\". . .", fileName);

        String fileNotFoundMessage = "Arquivo não encontrado: " + fileName;

        try {
            Path filePath = path.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            log.info("Verificando se o arquivo existe");

            if (resource.exists()) {
                log.info("Retornando o arquivo existente");

                return resource;
            } else {
                throw new CustomFileNotFoundException(fileNotFoundMessage);
            }

        } catch (MalformedURLException ex) {
            throw new CustomFileNotFoundException(fileNotFoundMessage, ex);
        }

    }

    private String storeFile(MultipartFile file, Path path) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        log.info("Tentando fazer o upload do arquivo \"{}\"", fileName);

        return storeFile(file, path, fileName);
    }

    private String storeFile(MultipartFile file, Path path, String fileName) {
        try {

            log.info("Verificando se o nome do arquivo contém caracteres inválidos");

            if (fileName.contains("..")) {
                throw new FileStorageException("Desculpa, mas o nome do arquivo possuí sequências de caminho inválidas: " + fileName);
            }

            Path targetLocation = getTargetLocation(path, fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("Upload realizado com sucesso!");

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
        log.info("Criando os diretórios necessários. . .");

        Path drinksPath = fileStoragePath.resolve(fileStorageProperties.getDrinksDir());

        List.of(fileStoragePath, drinksPath, usersPath).forEach((path) -> {
            if (!Files.exists(path)) {
                try {
                    Files.createDirectory(path);
                    log.info("Diretório foi criado: " + path);
                } catch (IOException ex) {
                    throw new FileStorageException("Não foi possível criar o diretório: " + path, ex);
                }
            }
        });

        log.info("Diretórios foram criados com sucesso!");
    }

    private void verifyIfIsAnImage(MultipartFile file) {
        log.info("Verificando se o arquivo enviado contém o content-type válido.");

        if (!IMAGES_TYPES.contains(file.getContentType())) {
            throw new FileStorageException("Arquivo não é uma imagem.");
        }
    }

}
