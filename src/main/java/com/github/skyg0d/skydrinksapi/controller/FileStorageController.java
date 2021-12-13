package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.responses.FileResponse;
import com.github.skyg0d.skydrinksapi.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/files")
public class FileStorageController {

    private final FileStorageService fileStorageService;

    @GetMapping
    @Operation(summary = "Mostra todos os arquivos com paginação", tags = "Files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<Page<String>> listAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(fileStorageService.listFiles(pageable));
    }

    @GetMapping("/list")
    @Operation(summary = "Mostra todos os arquivos", tags = "Files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<List<String>> listAll() {
        return ResponseEntity.ok(fileStorageService.listFiles());
    }

    @GetMapping(value = "/images/{fileName:.+}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    @Operation(summary = "Mostra a imagem especificada", tags = "Files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {
        return ResponseEntity.ok(fileStorageService.getImage(fileName));
    }

    @PostMapping(value = "/barmen/images", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Armazena e retorna informações de uma imagem", tags = "Files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<FileResponse> uploadImage(@RequestParam MultipartFile file) {
        String fileName = fileStorageService.storeImage(file);

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

    @PostMapping(value = "/barmen/multiple-images", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Armazena e retorna informações de várias imagens", tags = "Files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<FileResponse>> uploadMultipleImages(@RequestParam List<MultipartFile> files) {
        Map<String, MultipartFile> filesName = fileStorageService.storeImages(files);

        List<FileResponse> responses = filesName.keySet().stream().map((fileName) -> {
            MultipartFile file = filesName.get(fileName);

            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/files/images/")
                    .path(fileName)
                    .toUriString();

            return FileResponse.builder()
                    .fileName(fileName)
                    .fileDownloadUri(fileDownloadUri)
                    .fileType(file.getContentType())
                    .size(file.getSize())
                    .build();
        }).collect(Collectors.toList());

        return new ResponseEntity<>(responses, HttpStatus.CREATED);
    }

    @DeleteMapping("/barmen/images/{fileName:.+}")
    @Operation(summary = "Delete uma imagem", tags = "Files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando a imagem não existe no servidor"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteImage(@PathVariable String fileName) {
        fileStorageService.deleteImage(fileName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
