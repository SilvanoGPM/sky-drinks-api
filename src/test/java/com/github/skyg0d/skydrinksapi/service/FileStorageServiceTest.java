package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.exception.CustomFileNotFoundException;
import com.github.skyg0d.skydrinksapi.exception.FileStorageException;
import com.github.skyg0d.skydrinksapi.property.FileStorageProperties;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for FileStorageService")
class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp(@TempDir Path uploadDir, @TempDir Path drinksDir, @TempDir Path usersDir) {
        FileStorageProperties properties = new FileStorageProperties();

        properties.setUploadDir(uploadDir.toAbsolutePath().toString());
        properties.setDrinksDir(drinksDir.toAbsolutePath().toString());
        properties.setUsersDir(usersDir.toAbsolutePath().toString());

        this.fileStorageService = new FileStorageService(properties);
    }

    @Test
    @DisplayName("storeFile returns name of uploaded file when successful")
    void storeFile_ReturnsNameOfUploadedFile_WhenSuccessful() {
        MockMultipartFile multipartFile = new MockMultipartFile("file.txt", "file.txt", MediaType.TEXT_PLAIN_VALUE, "SkyG0D".getBytes());

        String fileName = fileStorageService.storeFile(multipartFile);

        assertThat(fileName)
                .isNotNull()
                .isEqualTo(multipartFile.getOriginalFilename());
    }

    @Test
    @DisplayName("storeFile returns name of uploaded file with additional number when file already exists")
    void storeFile_ReturnsNameOfUploadedImageWithAdditionalNumber_WhenFileAlreadyExists() throws IOException {
        InputStream inputStream1 = new FileInputStream("./test-files/drink.jpeg");

        MockMultipartFile multipartFile1 = new MockMultipartFile("drink.jpeg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, inputStream1);

        fileStorageService.storeFile(multipartFile1);

        InputStream inputStream2 = new FileInputStream("./test-files/drink.jpeg");

        MockMultipartFile multipartFile2 = new MockMultipartFile("drink.jpeg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, inputStream2);

        String fileName = fileStorageService.storeFile(multipartFile2);

        String[] fileParts = multipartFile2.getOriginalFilename().split("\\.");

        String expectedFileName = String.format("%s_%d.%s", fileParts[0], 1, fileParts[1]);

        assertThat(fileName)
                .isNotNull()
                .isEqualTo(expectedFileName);
    }

    @Test
    @DisplayName("storeFiles returns map of uploaded files when successful")
    void storeFiles_ReturnsMapOfUploadedFiles_WhenSuccessful() throws IOException {
        InputStream inputStream = new FileInputStream("./test-files/drink.jpeg");
        InputStream inputStream2 = new FileInputStream("./test-files/drink2.jpg");

        MockMultipartFile multipartFile = new MockMultipartFile("drink.jpg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, inputStream);
        MockMultipartFile multipartFile2 = new MockMultipartFile("drink2.jpg", "drink2.jpg", MediaType.IMAGE_JPEG_VALUE, inputStream2);

        Map<String, MultipartFile> filesName = fileStorageService.storeFiles(List.of(multipartFile, multipartFile2));

        assertThat(filesName)
                .isNotEmpty()
                .containsKeys(multipartFile.getOriginalFilename(), multipartFile2.getOriginalFilename());
    }

    @Test
    @DisplayName("storageImage returns name of uploaded images when successful")
    void storageImage_ReturnsNameOfUploadedImages_WhenSuccessful() throws IOException {
        InputStream inputStream = new FileInputStream("./test-files/drink.jpeg");

        MockMultipartFile multipartFile = new MockMultipartFile("drink.jpeg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, inputStream);

        String fileName = fileStorageService.storeDrinkImage(multipartFile);

        assertThat(fileName)
                .isNotNull()
                .isEqualTo(multipartFile.getOriginalFilename());
    }

    @Test
    @DisplayName("storageImages returns map of uploaded image when successful")
    void storageImages_ReturnsMapOfUploadedImage_WhenSuccessful() throws IOException {
        InputStream inputStream = new FileInputStream("./test-files/drink.jpeg");
        InputStream inputStream2 = new FileInputStream("./test-files/drink2.jpg");

        MockMultipartFile multipartFile = new MockMultipartFile("drink.jpg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, inputStream);
        MockMultipartFile multipartFile2 = new MockMultipartFile("drink2.jpg", "drink2.jpg", MediaType.IMAGE_JPEG_VALUE, inputStream2);

        Map<String, MultipartFile> filesName = fileStorageService.storeImages(List.of(multipartFile, multipartFile2));

        assertThat(filesName)
                .isNotEmpty()
                .containsKeys(multipartFile.getOriginalFilename(), multipartFile2.getOriginalFilename());
    }

    @Test
    @DisplayName("listFiles returns list of files path when successful")
    void listFiles_ReturnsListOfFilesPath_WhenSuccessful() {
        MockMultipartFile multipartFile = new MockMultipartFile("file.txt", "file.txt", MediaType.TEXT_PLAIN_VALUE, "SkyG0D".getBytes());

        String fileName = fileStorageService.storeFile(multipartFile);

        List<String> filesPath = fileStorageService.listFiles();

        assertThat(filesPath)
                .isNotEmpty()
                .hasSize(1)
                .contains("/" + fileName);
    }

    @Test
    @DisplayName("listFiles throws FileStorageException when path does not exists")
    void listFiles_ThrowsFileStorageException_WhenPathDoesNotExists(@TempDir Path uploadDir, @TempDir Path drinkDir, @TempDir Path usersDir) throws IOException {
        FileStorageProperties properties = new FileStorageProperties();

        properties.setUploadDir(uploadDir.toAbsolutePath().toString());
        properties.setDrinksDir(drinkDir.toAbsolutePath().toString());
        properties.setUsersDir(usersDir.toAbsolutePath().toString());

        this.fileStorageService = new FileStorageService(properties);

        FileUtils.deleteDirectory(uploadDir.toFile());
        FileUtils.deleteDirectory(drinkDir.toFile());
        FileUtils.deleteDirectory(usersDir.toFile());

        assertThatExceptionOfType(FileStorageException.class)
                .isThrownBy(() -> fileStorageService.listFiles());
    }

    @Test
    @DisplayName("listFiles returns list of files path inside page object when successful")
    void listFiles_ReturnsListOfFilesPathInsideObjectPage_WhenSuccessful() {
        MockMultipartFile multipartFile = new MockMultipartFile("file.txt", "file.txt", MediaType.TEXT_PLAIN_VALUE, "SkyG0D".getBytes());

        String fileName = fileStorageService.storeFile(multipartFile);

        Page<String> filesPage = fileStorageService.listFiles(PageRequest.of(0, 1));

        assertThat(filesPage)
                .isNotEmpty()
                .hasSize(1)
                .contains("/" + fileName);
    }

//    @Test
//    @DisplayName("getImage returns bytes of image when successful")
//    void getImage_ReturnsBytesOfImage_WhenSuccessful() throws IOException {
//        byte[] imageBytes = new FileInputStream("./test-files/drink.jpeg").readAllBytes();
//
//        MockMultipartFile multipartFile = new MockMultipartFile("drink.jpeg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, imageBytes);
//
//        String fileName = fileStorageService.storeProfilePicture(multipartFile, ApplicationUserCreator.createValidApplicationUser());
//
//        assertThat(image)
//                .isNotEmpty()
//                .isEqualTo(imageBytes);
//    }

    @Test
    @DisplayName("loadFileAsResource returns an resource when successful")
    void loadFileAsResource_ReturnsAnResource_WhenSuccessful() {
        byte[] messageBytes = "mensagem".getBytes();

        MockMultipartFile multipartFile = new MockMultipartFile("drink.jpeg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, messageBytes);

        String fileName = fileStorageService.storeFile(multipartFile);

        Resource resource = fileStorageService.loadFileAsResource(fileName);

        assertThat(resource).isNotNull();

        assertThat(resource.getFilename())
                .isNotNull()
                .isEqualTo(fileName);
    }

    @Test
    @DisplayName("loadFileAsResource throws CustomFileNotFoundException when file path does not exists")
    void loadFileAsResource_ThrowsCustomFileNotFoundException_WhenFilePathDoesNotExists(@TempDir Path uploadDir, @TempDir Path drinkDir, @TempDir Path usersDir) throws IOException {
        FileStorageProperties properties = new FileStorageProperties();

        properties.setUploadDir(uploadDir.toAbsolutePath().toString());
        properties.setDrinksDir(drinkDir.toAbsolutePath().toString());
        properties.setUsersDir(usersDir.toAbsolutePath().toString());

        this.fileStorageService = new FileStorageService(properties);

        FileUtils.deleteDirectory(uploadDir.toFile());
        FileUtils.deleteDirectory(drinkDir.toFile());
        FileUtils.deleteDirectory(usersDir.toFile());

        assertThatExceptionOfType(CustomFileNotFoundException.class)
                .isThrownBy(() -> fileStorageService.loadFileAsResource(""));

    }

    @Test
    @DisplayName("deleteFile removes file when successful")
    void deleteFile_RemovesFile_WhenSuccessful() {
        MockMultipartFile multipartFile = new MockMultipartFile("file.txt", "file.txt", MediaType.TEXT_PLAIN_VALUE, "SkyG0D".getBytes());

        String fileName = fileStorageService.storeFile(multipartFile);

        fileStorageService.deleteFile(fileName);

        List<String> files = fileStorageService.listFiles();

        assertThat(files).isEmpty();
    }

    @Test
    @DisplayName("deleteImage removes image when successful")
    void deleteImage_RemovesImage_WhenSuccessful() throws IOException {
        byte[] imageBytes = new FileInputStream("./test-files/drink.jpeg").readAllBytes();

        MockMultipartFile multipartFile = new MockMultipartFile("drink.jpeg", "drink.jpeg", MediaType.IMAGE_JPEG_VALUE, imageBytes);

        String fileName = fileStorageService.storeFile(multipartFile);

        fileStorageService.deleteDrinkImage(fileName);

        List<String> files = fileStorageService.listFiles();

        assertThat(files).isEmpty();
    }

    @Test
    @DisplayName("storeImage throws FileStorageException when file path contains unsupported chars sequences")
    void storeImage_ThrowsFileStorageException_WhenFilePathContainsUnsupportedCharsSequences() {
        MockMultipartFile multipartFile = new MockMultipartFile("../file.txt", "../file.txt", MediaType.TEXT_PLAIN_VALUE, "SkyG0D".getBytes());

        assertThatExceptionOfType(FileStorageException.class)
                .isThrownBy(() -> fileStorageService.storeFile(multipartFile));
    }

    @Test
    @DisplayName("storeImage throws FileStorageException when file is not an image")
    void storeImage_ThrowsFileStorageException_WhenFileIsNotAnImage() {
        MockMultipartFile multipartFile = new MockMultipartFile("drink.txt", "drink.txt", MediaType.TEXT_PLAIN_VALUE, "SkyG0D".getBytes());

        assertThatExceptionOfType(FileStorageException.class)
                .isThrownBy(() -> fileStorageService.storeDrinkImage(multipartFile));
    }

    @Test
    @DisplayName("loadFileAsResource throws CustomFileNotFoundException when file is not found")
    void loadFileAsResource_ThrowsCustomFileNotFoundException_WhenFileIsNotFound() {
        assertThatExceptionOfType(CustomFileNotFoundException.class)
                .isThrownBy(() -> fileStorageService.loadFileAsResource("jwqjfqjg"));
    }

    @Test
    @DisplayName("deleteFile throws CustomFileNotFoundException when file is not found")
    void deleteFile_ThrowsCustomFileNotFoundException_WhenFileIsNotFound() {
        assertThatExceptionOfType(CustomFileNotFoundException.class)
                .isThrownBy(() -> fileStorageService.deleteFile("jwqjfqjg"));
    }

}
