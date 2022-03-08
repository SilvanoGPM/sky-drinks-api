package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.requests.NewPasswordPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.RequestPasswordReset;
import com.github.skyg0d.skydrinksapi.requests.VerifyTokenPostRequestBody;
import com.github.skyg0d.skydrinksapi.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/password-reset")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/request")
    @Operation(summary = "Realiza o pedido de restauração de senha", tags = "Password Reset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<Void> requestPasswordReset(@Valid @RequestBody RequestPasswordReset requestPasswordReset) {
        passwordResetService.create(requestPasswordReset.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/verify")
    @Operation(summary = "Realiza a verificação de código para restauração de senha", tags = "Password Reset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<Void> verifyToken(@Valid @RequestBody VerifyTokenPostRequestBody verifyTokenPostRequestBody) {
        passwordResetService.tokenIsValid(verifyTokenPostRequestBody.getToken(), verifyTokenPostRequestBody.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/confirm")
    @Operation(summary = "Realiza a confirmação de restauração de senha", tags = "Password Reset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<Void> confirmPasswordReset(@Valid @RequestBody NewPasswordPostRequestBody newPasswordPostRequestBody) {
        passwordResetService.resetPassword(newPasswordPostRequestBody);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
