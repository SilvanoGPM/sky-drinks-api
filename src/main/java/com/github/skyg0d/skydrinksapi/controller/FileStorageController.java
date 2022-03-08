package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.responses.FileResponse;
import com.github.skyg0d.skydrinksapi.service.FileStorageService;
import com.github.skyg0d.skydrinksapi.util.AuthUtil;
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

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/files")
public class FileStorageController {

    private final FileStorageService fileStorageService;
    private final AuthUtil authUtil;

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

    @GetMapping(value = "/drinks/{fileName:.+}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    @Operation(summary = "Mostra a imagem da bebida especificada", tags = "Files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<byte[]> getDrinkImage(@PathVariable String fileName) {
        return ResponseEntity.ok(fileStorageService.getDrinkImage(fileName));
    }

    @GetMapping(value = "/users/{fileName:.+}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    @Operation(summary = "Mostra a imagem do usuário especificado", tags = "Files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<byte[]> getUserImage(@PathVariable String fileName) {
        return ResponseEntity.ok(fileStorageService.getUserImage(fileName));
    }

    @PostMapping(value = "/barmen/drinks", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Armazena e retorna informações de uma imagem de bebida", tags = "Files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<FileResponse> uploadDrinkImage(@RequestParam MultipartFile file) {
        String fileName = fileStorageService.storeDrinkImage(file);

        String fileDownloadUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/files/drinks/")
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

    @PostMapping(value = "/all/user-picture", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Armazena e retorna informações da foto do usuário", tags = "Files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<FileResponse> uploadUserPicture(@RequestParam MultipartFile file, Principal principal) {
        String fileName = fileStorageService.storeUserImage(file, authUtil.getUser(principal));

        String fileDownloadUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/files/users/")
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

    @PostMapping(value = "/barmen/multiple-drinks", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Armazena e retorna informações de várias imagens", tags = "Files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<FileResponse>> uploadMultipleDrinksImages(@RequestParam List<MultipartFile> files) {
        Map<String, MultipartFile> filesName = fileStorageService.storeDrinksImages(files);

        List<FileResponse> responses = filesName.keySet().stream().map((fileName) -> {
            MultipartFile file = filesName.get(fileName);

            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/files/drinks/")
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

    @DeleteMapping("/barmen/drinks/{fileName:.+}")
    @Operation(summary = "Deleta uma imagem de bebida", tags = "Files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando a imagem não existe no servidor"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteDrinkImage(@PathVariable String fileName) {
        fileStorageService.deleteDrinkImage(fileName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/barmen/all/{fileName:.+}")
    @Operation(summary = "Deleta uma imagem de usuário", tags = "Files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando a imagem não existe no servidor"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteUserImage(@PathVariable String fileName, Principal principal) {
        fileStorageService.deleteUserImage(fileName, authUtil.getUser(principal));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
