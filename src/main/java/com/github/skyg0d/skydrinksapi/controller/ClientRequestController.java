package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.enums.Roles;
import com.github.skyg0d.skydrinksapi.parameters.ClientRequestParameters;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPutRequestBody;
import com.github.skyg0d.skydrinksapi.service.ClientRequestService;
import com.github.skyg0d.skydrinksapi.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

    @GetMapping("/waiter")
    public ResponseEntity<Page<ClientRequest>> listAll(Pageable pageable) {
        return ResponseEntity.ok(clientRequestService.listAll(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ClientRequest>> search(ClientRequestParameters parameters, Pageable pageable, Principal principal) {
        return ResponseEntity.ok(clientRequestService.search(parameters, pageable, authUtil.getUser(principal)));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ClientRequest> findById(@PathVariable UUID uuid) {
        return ResponseEntity.ok((clientRequestService.findByIdOrElseThrowBadRequestException(uuid)));
    }

    @PostMapping("/user")
    public ResponseEntity<ClientRequest> save(@RequestBody @Valid ClientRequestPostRequestBody clientRequestPostRequestBody, Principal principal) {
        ApplicationUser user = authUtil.getUser(principal);

        if (!user.getRole().contains(Roles.USER.getName())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(clientRequestService.save(clientRequestPostRequestBody, user), HttpStatus.CREATED);
    }

    @PutMapping("/waiter-or-user")
    public ResponseEntity<Void> replace(@RequestBody @Valid ClientRequestPutRequestBody clientRequestPutRequestBody, Principal principal) {
        clientRequestService.replace(clientRequestPutRequestBody, authUtil.getUser(principal));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/waiter-or-user/{uuid}")
    public ResponseEntity<ClientRequest> finishRequest(@PathVariable UUID uuid, Principal principal) {
        return ResponseEntity.ok(clientRequestService.finishRequest(uuid, authUtil.getUser(principal)));
    }

    @DeleteMapping("/waiter-or-user/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid, Principal principal) {
        clientRequestService.delete(uuid, authUtil.getUser(principal));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
