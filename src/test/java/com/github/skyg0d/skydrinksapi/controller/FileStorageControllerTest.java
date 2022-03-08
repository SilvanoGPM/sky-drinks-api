package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.property.FileStorageProperties;
import com.github.skyg0d.skydrinksapi.responses.FileResponse;
import com.github.skyg0d.skydrinksapi.service.FileStorageService;
import com.github.skyg0d.skydrinksapi.util.AuthUtil;
import com.github.skyg0d.skydrinksapi.util.file.FileStoragePropertiesCreator;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserCreator;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for FileStorageController")
class FileStorageControllerTest {

    private FileStorageController fileStorageController;

    @Mock
    private AuthUtil authUtilMock;

    @BeforeEach
    void setUp() {
        BDDMockito
                .when(authUtilMock.getUser(ArgumentMatchers.any(Principal.class)))
                .thenReturn(ApplicationUserCreator.createValidApplicationUser());

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        FileStorageProperties properties = FileStoragePropertiesCreator.createFileStorageProperties();

        fileStorageController = new FileStorageController(new FileStorageService(properties), authUtilMock);
    }

    @Test
    @DisplayName("uploadDrinkImage returns name of uploaded file when successful")
    void uploadDrinkImage_ReturnsFileResponse_WhenSuccessful() throws IOException {
        byte[] imageBytes = new FileInputStream("./test-files/drink.jpeg").readAllBytes();

        MockMultipartFile multipartFile = new MockMultipartFile("drink.jpeg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, imageBytes);

        ResponseEntity<FileResponse> entity = fileStorageController.uploadDrinkImage(multipartFile);

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
    @DisplayName("uploadMultipleDrinksImages returns list of uploaded file when successful")
    void uploadMultipleDrinksImages_ReturnsFileResponse_WhenSuccessful() throws IOException {
        byte[] imageBytes = new FileInputStream("./test-files/drink.jpeg").readAllBytes();
        byte[] imageBytes2 = new FileInputStream("./test-files/drink2.jpg").readAllBytes();

        MockMultipartFile multipartFile = new MockMultipartFile("drink.jpeg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, imageBytes);
        MockMultipartFile multipartFile2 = new MockMultipartFile("drink2.jpg", "drink2.jpg", MediaType.IMAGE_JPEG_VALUE, imageBytes2);

        ResponseEntity<List<FileResponse>> entity = fileStorageController.uploadMultipleDrinksImages(List.of(multipartFile, multipartFile2));

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
    @DisplayName("uploadUserImage returns name of uploaded file when successful")
    void uploadUserImage_ReturnsFileResponse_WhenSuccessful() throws IOException {
        byte[] imageBytes = new FileInputStream("./test-files/user.png").readAllBytes();

        MockMultipartFile multipartFile = new MockMultipartFile("user.png", "user.png", MediaType.IMAGE_PNG_VALUE, imageBytes);

        Principal principalMock = Mockito.mock(Principal.class);

        ResponseEntity<FileResponse> entity = fileStorageController.uploadUserImage(multipartFile, principalMock);

        assertThat(entity).isNotNull();

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody().getFileName()).isNotNull();

        String fileName = ApplicationUserCreator.createValidApplicationUser().getUuid().toString();

        String fileExtension = "." + FilenameUtils.getExtension(entity.getBody().getFileName());

        assertThat(entity.getBody().getFileName())
                .isEqualTo(fileName + fileExtension);
    }

    @Test
    @DisplayName("listAll returns list of files path when successful")
    void listAll_ReturnsListOfFilesPath_WhenSuccessful() throws IOException {
        InputStream inputStream = new FileInputStream("./test-files/drink.jpeg");

        MockMultipartFile multipartFile = new MockMultipartFile("drink.jpeg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, inputStream);

        FileResponse response = fileStorageController.uploadDrinkImage(multipartFile).getBody();

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
                .anyMatch((path) -> path.contains(response.getFileName()));
    }

    @Test
    @DisplayName("listAll returns list of files path when successful")
    void listAll_ReturnsListOfFilesPathInsideObjectPage_WhenSuccessful() throws IOException {
        InputStream inputStream = new FileInputStream("./test-files/drink.jpeg");

        MockMultipartFile multipartFile = new MockMultipartFile("drink.jpeg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, inputStream);

        FileResponse response = fileStorageController.uploadDrinkImage(multipartFile).getBody();

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
                .anyMatch((path) -> path.contains(response.getFileName()));
    }

    @Test
    @DisplayName("getDrinkImage returns bytes of image when successful")
    void getDrinkImage_ReturnsBytesOfImage_WhenSuccessful() throws IOException {
        byte[] imageBytes = new FileInputStream("./test-files/drink.jpeg").readAllBytes();

        MockMultipartFile multipartFile = new MockMultipartFile("drink.jpeg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, imageBytes);

        FileResponse response = fileStorageController.uploadDrinkImage(multipartFile).getBody();

        assertThat(response).isNotNull();

        assertThat(response.getFileName()).isNotNull();

        ResponseEntity<byte[]> entity = fileStorageController.getDrinkImage(response.getFileName());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotEmpty()
                .isEqualTo(imageBytes);
    }

    @Test
    @DisplayName("getUserImage returns bytes of image when successful")
    void getUserImage_ReturnsBytesOfImage_WhenSuccessful() throws IOException {
        byte[] imageBytes = new FileInputStream("./test-files/user.png").readAllBytes();

        MockMultipartFile multipartFile = new MockMultipartFile("user.png", "user.png", MediaType.IMAGE_PNG_VALUE, imageBytes);

        Principal principalMock = Mockito.mock(Principal.class);

        FileResponse response = fileStorageController.uploadUserImage(multipartFile, principalMock).getBody();

        assertThat(response).isNotNull();

        assertThat(response.getFileName()).isNotNull();

        ResponseEntity<byte[]> entity = fileStorageController.getUserImage(response.getFileName());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotEmpty()
                .isEqualTo(imageBytes);
    }

    @Test
    @DisplayName("deleteDrinkImage removes image when successful")
    void deleteDrinkImage_RemovesImage_WhenSuccessful() throws IOException {
        byte[] imageBytes = new FileInputStream("./test-files/drink.jpeg").readAllBytes();

        MockMultipartFile multipartFile = new MockMultipartFile("drink.jpeg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, imageBytes);

        FileResponse response = fileStorageController.uploadDrinkImage(multipartFile).getBody();

        assertThat(response).isNotNull();

        assertThat(response.getFileName()).isNotNull();

        ResponseEntity<Void> entity = fileStorageController.deleteDrinkImage(response.getFileName());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("deleteUserImage removes image when successful")
    void deleteUserImage_RemovesImage_WhenSuccessful() throws IOException {
        byte[] imageBytes = new FileInputStream("./test-files/user.png").readAllBytes();

        MockMultipartFile multipartFile = new MockMultipartFile("user.png", "user.png", MediaType.IMAGE_PNG_VALUE, imageBytes);

        Principal principalMock = Mockito.mock(Principal.class);

        FileResponse response = fileStorageController.uploadUserImage(multipartFile, principalMock).getBody();

        assertThat(response).isNotNull();

        assertThat(response.getFileName()).isNotNull();

        ResponseEntity<Void> entity = fileStorageController.deleteUserImage(response.getFileName(), principalMock);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

}