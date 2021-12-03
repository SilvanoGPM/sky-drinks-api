package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.parameters.DrinkParameters;
import com.github.skyg0d.skydrinksapi.requests.DrinkPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.DrinkPutRequestBody;
import com.github.skyg0d.skydrinksapi.service.DrinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/drinks")
@Log4j2
public class DrinkController {

    private final DrinkService drinkService;

    @GetMapping
    @Operation(summary = "Retorna todos os drinks com paginação", tags = "Drinks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<Page<Drink>> listAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(drinkService.listAll(pageable));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Retorna o drink especificado" , tags = "Drinks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando o drink não existe no banco de dados"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<Drink> findById(@PathVariable UUID uuid) {
        return ResponseEntity.ok(drinkService.findByIdOrElseThrowBadRequestException(uuid));
    }

    @GetMapping("/search")
    @Operation(summary = "Retorna os drinks encontrados com paginação" , tags = "Drinks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<Page<Drink>> search(@ParameterObject DrinkParameters drinkParameters, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(drinkService.search(drinkParameters, pageable));
    }

    @PostMapping("/barmen")
    @Operation(summary = "Cria um novo usuário e retorna seus dados" , tags = "Drinks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Drink> save(@RequestBody @Valid DrinkPostRequestBody drinkPostRequestBody) {
        return new ResponseEntity<>(drinkService.save(drinkPostRequestBody), HttpStatus.CREATED);
    }

    @PutMapping("/barmen")
    @Operation(summary = "Atualiza um usuário", tags = "Drinks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando o drink não existe no banco de dados"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> replace(@RequestBody @Valid DrinkPutRequestBody drinkPutRequestBody) {
        drinkService.replace(drinkPutRequestBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/barmen/{uuid}")
    @Operation(summary = "Remove um usuário", tags = "Drinks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando o drink não existe no banco de dados"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        drinkService.delete(uuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
