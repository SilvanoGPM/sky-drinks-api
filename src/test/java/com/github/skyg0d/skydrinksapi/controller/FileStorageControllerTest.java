package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.property.FileStorageProperties;
import com.github.skyg0d.skydrinksapi.responses.FileResponse;
import com.github.skyg0d.skydrinksapi.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for FileStorageController")
class FileStorageControllerTest {

    private FileStorageController fileStorageController;

    @BeforeEach
    void setUp(@TempDir Path uploadDir, @TempDir Path imagedDir) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        FileStorageProperties properties = new FileStorageProperties();

        properties.setUploadDir(uploadDir.toAbsolutePath().toString());
        properties.setImagesDir(imagedDir.toAbsolutePath().toString());

        fileStorageController = new FileStorageController(new FileStorageService(properties));
    }

    @Test
    @DisplayName("uploadImage returns name of uploaded file when successful")
    void updloadImage_ReturnsFileResponse_WhenSuccessful() throws IOException {
        byte[] imageBytes = new FileInputStream("./test-files/drink.jpeg").readAllBytes();

        MockMultipartFile multipartFile = new MockMultipartFile("drink.jpeg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, imageBytes);

        ResponseEntity<FileResponse> entity = fileStorageController.uploadImage(multipartFile);

        assertThat(entity).isNotNull();

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody().getFileName())
                .isNotNull()
                .isEqualTo(multipartFile.getOriginalFilename());
    }

    @Test
    @DisplayName("uploadMultipleImages returns list of uploaded file when successful")
    void uploadMultipleImages_ReturnsFileResponse_WhenSuccessful() throws IOException {
        byte[] imageBytes = new FileInputStream("./test-files/drink.jpeg").readAllBytes();
        byte[] imageBytes2 = new FileInputStream("./test-files/drink2.jpg").readAllBytes();

        MockMultipartFile multipartFile = new MockMultipartFile("drink.jpeg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, imageBytes);
        MockMultipartFile multipartFile2 = new MockMultipartFile("drink2.jpg", "drink2.jpg", MediaType.IMAGE_JPEG_VALUE, imageBytes2);

        ResponseEntity<List<FileResponse>> entity = fileStorageController.uploadMultipleImages(List.of(multipartFile, multipartFile2));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody())
                .isNotEmpty()
                .hasSize(2);

        assertThat(entity.getBody().get(0)).isNotNull();

        assertThat(entity.getBody().get(0).getFileName())
                .isNotNull()
                .isEqualTo(multipartFile.getOriginalFilename());

        assertThat(entity.getBody().get(1)).isNotNull();

        assertThat(entity.getBody().get(1).getFileName())
                .isNotNull()
                .isEqualTo(multipartFile2.getOriginalFilename());
    }

    @Test
    @DisplayName("listAll returns list of files path when successful")
    void listAll_ReturnsListOfFilesPath_WhenSuccessful() throws IOException {
        InputStream inputStream = new FileInputStream("./test-files/drink.jpeg");

        MockMultipartFile multipartFile = new MockMultipartFile("drink.jpeg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, inputStream);

        FileResponse response = fileStorageController.uploadImage(multipartFile).getBody();

        ResponseEntity<List<String>> entity = fileStorageController.listAll();

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(response).isNotNull();

        assertThat(response.getFileName()).isNotNull();

        assertThat(entity.getBody())
                .isNotEmpty()
                .hasSize(1)
                .contains("/" + response.getFileName());
    }

    @Test
    @DisplayName("listAll returns list of files path when successful")
    void listAll_ReturnsListOfFilesPathInsideObjectPage_WhenSuccessful() throws IOException {
        InputStream inputStream = new FileInputStream("./test-files/drink.jpeg");

        MockMultipartFile multipartFile = new MockMultipartFile("drink.jpeg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, inputStream);

        FileResponse response = fileStorageController.uploadImage(multipartFile).getBody();

        ResponseEntity<Page<String>> entity = fileStorageController.listAll(PageRequest.of(0, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(response).isNotNull();

        assertThat(response.getFileName()).isNotNull();

        assertThat(entity.getBody())
                .isNotEmpty()
                .hasSize(1)
                .contains("/" + response.getFileName());
    }

    @Test
    @DisplayName("getImage returns bytes of image when successful")
    void getImage_ReturnsBytesOfImage_WhenSuccessful() throws IOException {
        byte[] imageBytes = new FileInputStream("./test-files/drink.jpeg").readAllBytes();

        MockMultipartFile multipartFile = new MockMultipartFile("drink.jpeg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, imageBytes);

        FileResponse response = fileStorageController.uploadImage(multipartFile).getBody();

        assertThat(response).isNotNull();

        assertThat(response.getFileName()).isNotNull();

        ResponseEntity<byte[]> entity = fileStorageController.getImage(response.getFileName());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotEmpty()
                .isEqualTo(imageBytes);
    }

    @Test
    @DisplayName("deleteImage removes image when successful")
    void deleteImage_RemovesImage_WhenSuccessful() throws IOException {
        byte[] imageBytes = new FileInputStream("./test-files/drink.jpeg").readAllBytes();

        MockMultipartFile multipartFile = new MockMultipartFile("drink.jpeg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, imageBytes);

        FileResponse response = fileStorageController.uploadImage(multipartFile).getBody();

        assertThat(response).isNotNull();

        assertThat(response.getFileName()).isNotNull();

        ResponseEntity<Void> entity = fileStorageController.deleteImage(response.getFileName());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

}