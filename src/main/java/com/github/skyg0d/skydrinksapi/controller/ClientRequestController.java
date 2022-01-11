package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.domain.*;
import com.github.skyg0d.skydrinksapi.enums.ClientRequestStatus;
import com.github.skyg0d.skydrinksapi.enums.Roles;
import com.github.skyg0d.skydrinksapi.parameters.ClientRequestParameters;
import com.github.skyg0d.skydrinksapi.property.WebSocketProperties;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPutRequestBody;
import com.github.skyg0d.skydrinksapi.service.ApplicationUserService;
import com.github.skyg0d.skydrinksapi.service.ClientRequestService;
import com.github.skyg0d.skydrinksapi.socket.domain.ClientRequestStatusChanged;
import com.github.skyg0d.skydrinksapi.socket.domain.SocketMessage;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/requests")
@Log4j2
public class ClientRequestController {

    private final ClientRequestService clientRequestService;
    private final ApplicationUserService applicationUserService;
    private final AuthUtil authUtil;
    private final SimpMessagingTemplate template;
    private final WebSocketProperties webSocketProperties;

    private boolean sendNotificationScheduled;

    @GetMapping("/staff")
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

    @GetMapping("/staff/search")
    @Operation(summary = "Retorna os pedidos encontrados com paginação", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<ClientRequest>> search(@ParameterObject ClientRequestParameters parameters, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(clientRequestService.search(parameters, pageable));
    }

    @GetMapping("/user/top-five-drinks")
    @Operation(summary = "Retorna as cinco bebidas que mais aparecem nos seus pedidos", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<ClientRequestDrinkCount>> getTopFiveDrinks(Principal principal) {
        return ResponseEntity.ok(clientRequestService.getMyTopFiveDrinks(authUtil.getUser(principal)));
    }

    @GetMapping("/admin/top-five-drinks/{uuid}")
    @Operation(summary = "Retorna as cinco bebidas que mais aparecem de um determinado usuário", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<ClientRequestDrinkCount>> getTopFiveDrinks(@PathVariable UUID uuid) {
        return ResponseEntity.ok(clientRequestService.getTopFiveDrinks(uuid));
    }

    @GetMapping("/admin/top-drinks")
    @Operation(summary = "Retorna as bebidas que mais aparecem nos pedidos", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<ClientRequestDrinkCount>> getTopDrinksInRequests(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(clientRequestService.getTopDrinksInRequests(pageable));
    }

    @GetMapping("/admin/most-canceled")
    @Operation(summary = "Retorna as bebidas que mais foram canceladas nos pedidos", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<ClientRequestDrinkCount>> mostCanceledDrinks(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(clientRequestService.mostCanceledDrinks(pageable));
    }

    @GetMapping("/admin/all-dates")
    @Operation(summary = "Retorna as datas que foram realizados pedidos", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<ClientRequestDate>> getAllDatesInRequests() {
        return ResponseEntity.ok(clientRequestService.getAllDatesInRequests());
    }

    @GetMapping("/user/total-of-drinks-alcoholic")
    @Operation(summary = "Retorna as todas as bebidas dos seus pedidos", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<ClientRequestAlcoholicDrinkCount>> getTotalOfDrinksGroupedByAlcoholic(Principal principal) {
        return ResponseEntity.ok(clientRequestService.getTotalOfDrinksGroupedByAlcoholic(authUtil.getUser(principal)));
    }

    @GetMapping("/user/my-requests")
    @Operation(summary = "Retorna os pedidos encontrados com paginação", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<ClientRequest>> searchMyRequests(@ParameterObject ClientRequestParameters parameters, @ParameterObject Pageable pageable, Principal principal) {
        return ResponseEntity.ok(clientRequestService.searchMyRequests(parameters, pageable, authUtil.getUser(principal)));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Retorna o pedido especificado", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando o pedido não existe no banco de dados"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<ClientRequest> findById(@PathVariable UUID uuid) {
        return ResponseEntity.ok((clientRequestService.findByIdOrElseThrowBadRequestException(uuid)));
    }

    @GetMapping("/all/all-blocked")
    @Operation(summary = "Retorna se todos os usuários estão bloqueados", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Boolean> getAllBlocked() {
        return ResponseEntity.ok((clientRequestService.getAllBlocked()));
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

        ClientRequest clientRequestSaved = clientRequestService.save(clientRequestPostRequestBody, user);

        requestsChanged();

        return new ResponseEntity<>(clientRequestSaved, HttpStatus.CREATED);
    }

    @PutMapping("/admin")
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
        requestsChanged();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/staff/finish/{uuid}")
    @Operation(summary = "Finaliza um pedido", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando o pedido não existe no banco de dados, ou o usuário não pode realizar essa ação"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ClientRequest> finishRequest(@PathVariable UUID uuid) {
        ClientRequest clientRequestFinished = clientRequestService.finishRequest(uuid);

        sendToUserRequestChanged(uuid, ClientRequestStatus.FINISHED.toString());

        return ResponseEntity.ok(clientRequestFinished);
    }

    @PatchMapping("/all/cancel/{uuid}")
    @Operation(summary = "Cancela um pedido", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando o pedido não existe no banco de dados, ou o usuário não pode realizar essa ação"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ClientRequest> cancelRequest(@PathVariable UUID uuid, Principal principal) {
        ClientRequest clientRequestFinished = clientRequestService.cancelRequest(uuid, authUtil.getUser(principal));

        sendToUserRequestChanged(uuid, ClientRequestStatus.CANCELED.toString());

        return ResponseEntity.ok(clientRequestFinished);
    }

    @PatchMapping("/staff/deliver/{uuid}")
    @Operation(summary = "Entrega um pedido", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando o pedido não existe no banco de dados, ou o usuário não pode realizar essa ação"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ClientRequest> deliverRequest(@PathVariable UUID uuid) {
        ClientRequest clientRequestFinished = clientRequestService.deliverRequest(uuid);

        sendToUserRequestChanged(uuid, "DELIVERED");

        return ResponseEntity.ok(clientRequestFinished);
    }

    @DeleteMapping("/admin/{uuid}")
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

        requestsChanged();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/admin/toggle-all-blocked")
    @Operation(summary = "Inverte se todos os usuários estão bloqueados ou não", tags = "Requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Boolean> toggleBlockAllRequests() {
        return ResponseEntity.ok(clientRequestService.toggleBlockAllRequests());
    }

    private void requestsChanged() {
        if (!sendNotificationScheduled) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    applicationUserService
                            .getStaffUsers()
                            .forEach((user) -> template.convertAndSend("/topic/updated/" + user.getEmail(), new SocketMessage("requests-changed")));

                    sendNotificationScheduled = false;
                }
            }, webSocketProperties.getSendClientRequestUpdateDelay());

            sendNotificationScheduled = true;
        }
    }

    private void sendToUserRequestChanged(UUID uuid, String message) {
        String email = clientRequestService.findByIdOrElseThrowBadRequestException(uuid).getUser().getEmail();

        ClientRequestStatusChanged clientRequestStatusChanged = ClientRequestStatusChanged
                .builder()
                .uuid(uuid)
                .message(message)
                .build();

        template.convertAndSend("/topic/request-changed/" + email, clientRequestStatusChanged);
    }

}
