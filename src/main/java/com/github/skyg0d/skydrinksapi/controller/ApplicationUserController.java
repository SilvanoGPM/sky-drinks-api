package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.parameters.ApplicationUserParameters;
import com.github.skyg0d.skydrinksapi.requests.ApplicationUserPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.ApplicationUserPutRequestBody;
import com.github.skyg0d.skydrinksapi.service.ApplicationUserService;
import com.github.skyg0d.skydrinksapi.util.AuthUtil;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<Page<ApplicationUser>> listAll(Pageable pageable) {
        return ResponseEntity.ok(applicationUserService.listAll(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ApplicationUser>> search(ApplicationUserParameters applicationUserParameters, Pageable pageable) {
        return ResponseEntity.ok(applicationUserService.search(applicationUserParameters, pageable));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApplicationUser> findById(@PathVariable UUID uuid) {
        return ResponseEntity.ok(applicationUserService.findByIdOrElseThrowBadRequestException(uuid));
    }

    @GetMapping("/find-by-email/{email}")
    public ResponseEntity<ApplicationUser> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(applicationUserService.findByEmail(email));
    }

    @GetMapping("/find-by-cpf/{cpf}")
    public ResponseEntity<ApplicationUser> findByCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(applicationUserService.findByCpf(cpf));
    }

    @PostMapping("/admin")
    public ResponseEntity<ApplicationUser> save(@Valid @RequestBody ApplicationUserPostRequestBody applicationUserPostRequestBody) {
        return new ResponseEntity<>(applicationUserService.save(applicationUserPostRequestBody), HttpStatus.CREATED);
    }

    @PutMapping("/user")
    public ResponseEntity<Void> replace(@Valid @RequestBody ApplicationUserPutRequestBody applicationUserPutRequestBody, Principal principal) {
        applicationUserService.replace(applicationUserPutRequestBody, authUtil.getUser(principal));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/user/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid, Principal principal) {
        applicationUserService.delete(uuid, authUtil.getUser(principal));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
