package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.parameters.ApplicationUserParameters;
import com.github.skyg0d.skydrinksapi.requests.ApplicationUserPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.ApplicationUserPutRequestBody;
import com.github.skyg0d.skydrinksapi.service.ApplicationUserService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class ApplicationUserController {

    private final ApplicationUserService applicationUserService;
    private final AuthUtil authUtil;

    @GetMapping
    @Operation(summary = "Retorna todos os usuários paginação", tags = "Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<Page<ApplicationUser>> listAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(applicationUserService.listAll(pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Retorna os usuários encontrados paginação", tags = "Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<Page<ApplicationUser>> search(@ParameterObject ApplicationUserParameters applicationUserParameters, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(applicationUserService.search(applicationUserParameters, pageable));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Retorna um usuário especificado", tags = "Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando o usuário não existe no banco de dados"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<ApplicationUser> findById(@PathVariable UUID uuid) {
        return ResponseEntity.ok(applicationUserService.findByIdOrElseThrowBadRequestException(uuid));
    }

    @GetMapping("/find-by-email/{email}")
    @Operation(summary = "Retorna um usuário especificado", tags = "Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando o usuário não existe no banco de dados"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<ApplicationUser> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(applicationUserService.findByEmail(email));
    }

    @GetMapping("/find-by-cpf/{cpf}")
    @Operation(summary = "Retorna um usuário especificado", tags = "Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando o usuário não existe no banco de dados"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<ApplicationUser> findByCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(applicationUserService.findByCpf(cpf));
    }

    @PostMapping("/admin")
    @Operation(summary = "Cria um usuário e retorna os dados", tags = "Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando o usuário não possui a ROLE_USER"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApplicationUser> save(@Valid @RequestBody ApplicationUserPostRequestBody applicationUserPostRequestBody) {
        return new ResponseEntity<>(applicationUserService.save(applicationUserPostRequestBody), HttpStatus.CREATED);
    }

    @PutMapping("/user")
    @Operation(summary = "Atualiza um usuário", tags = "Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando o usuário não existe no banco de dados"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> replace(@Valid @RequestBody ApplicationUserPutRequestBody applicationUserPutRequestBody, Principal principal) {
        applicationUserService.replace(applicationUserPutRequestBody, authUtil.getUser(principal));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/user/{uuid}")
    @Operation(summary = "Deleta um usuário", tags = "Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando o usuário não existe no banco de dados"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid, Principal principal) {
        applicationUserService.delete(uuid, authUtil.getUser(principal));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
