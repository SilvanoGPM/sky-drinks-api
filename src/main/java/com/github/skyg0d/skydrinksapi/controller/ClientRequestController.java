package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.enums.Roles;
import com.github.skyg0d.skydrinksapi.parameters.ClientRequestParameters;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPutRequestBody;
import com.github.skyg0d.skydrinksapi.service.ClientRequestService;
import com.github.skyg0d.skydrinksapi.util.AuthUtil;
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
import java.security.Principal;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/requests")
@Log4j2
public class ClientRequestController {

    private final ClientRequestService clientRequestService;
    private final AuthUtil authUtil;

    @GetMapping("/waiter-or-barmen")
    @Operation(summary = "Retorna todos os pedidos com paginação", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<ClientRequest>> listAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(clientRequestService.listAll(pageable));
    }

    @GetMapping("/all/search")
    @Operation(summary = "Retorna os pedidos encontrados com paginação", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<ClientRequest>> search(@ParameterObject ClientRequestParameters parameters, @ParameterObject Pageable pageable, Principal principal) {
        return ResponseEntity.ok(clientRequestService.search(parameters, pageable, authUtil.getUser(principal)));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Retorna o pedido especificado" , tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando o pedido não existe no banco de dados"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<ClientRequest> findById(@PathVariable UUID uuid) {
        return ResponseEntity.ok((clientRequestService.findByIdOrElseThrowBadRequestException(uuid)));
    }

    @PostMapping("/user")
    @Operation(summary = "Cria um pedido e retorna os dados", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ClientRequest> save(@RequestBody @Valid ClientRequestPostRequestBody clientRequestPostRequestBody, Principal principal) {
        ApplicationUser user = authUtil.getUser(principal);

        if (!user.getRole().contains(Roles.USER.getName())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(clientRequestService.save(clientRequestPostRequestBody, user), HttpStatus.CREATED);
    }

    @PutMapping("/all")
    @Operation(summary = "Atualiza um pedido", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando o pedido não existe no banco de dados, ou o usuário não pode realizar essa ação"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> replace(@RequestBody @Valid ClientRequestPutRequestBody clientRequestPutRequestBody, Principal principal) {
        clientRequestService.replace(clientRequestPutRequestBody, authUtil.getUser(principal));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/all/{uuid}")
    @Operation(summary = "Atualiza parcialmente um pedido", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando o pedido não existe no banco de dados, ou o usuário não pode realizar essa ação"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ClientRequest> finishRequest(@PathVariable UUID uuid, Principal principal) {
        return ResponseEntity.ok(clientRequestService.finishRequest(uuid, authUtil.getUser(principal)));
    }

    @DeleteMapping("/all/{uuid}")
    @Operation(summary = "Remove um pedido", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando o pedido não existe no banco de dados, ou o usuário não pode realizar essa ação"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid, Principal principal) {
        clientRequestService.delete(uuid, authUtil.getUser(principal));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
