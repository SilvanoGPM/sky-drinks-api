package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.property.FileStorageProperties;
import com.github.skyg0d.skydrinksapi.responses.FileResponse;
import com.github.skyg0d.skydrinksapi.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/files")
public class FileStorageController {

    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<Page<String>> list(Pageable pageable) {
        return ResponseEntity.ok(fileStorageService.listFiles(pageable));
    }

    @GetMapping("/all")
    public ResponseEntity<List<String>> listAll() {
        return ResponseEntity.ok(fileStorageService.listFiles());
    }

    @GetMapping(value = "/images/{fileName:.+}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {
        return ResponseEntity.ok(fileStorageService.getImage(fileName));
    }

    @PostMapping("/images")
    public ResponseEntity<FileResponse> uploadImage(@RequestParam MultipartFile file) {
        String fileName = fileStorageService.storageImage(file);

        String fileDownloadUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/files/images/")
                .path(fileName)
                .toUriString();

        FileResponse response = FileResponse.builder()
                .fileName(fileName)
                .fileDownloadUri(fileDownloadUri)
                .fileType(file.getContentType())
                .size(file.getSize())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/images/{fileName:.+}")
    public ResponseEntity<Void> deleteImage(@PathVariable String fileName) {
        fileStorageService.deleteImage(fileName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
