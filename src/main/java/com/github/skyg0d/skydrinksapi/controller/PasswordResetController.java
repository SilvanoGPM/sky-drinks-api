package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.requests.NewPasswordPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.RequestPasswordReset;
import com.github.skyg0d.skydrinksapi.requests.VerifyTokenPostRequestBody;
import com.github.skyg0d.skydrinksapi.service.PasswordResetService;
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
    public ResponseEntity<Void> requestPasswordReset(@Valid @RequestBody RequestPasswordReset requestPasswordReset) {
        passwordResetService.create(requestPasswordReset.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyToken(@Valid @RequestBody VerifyTokenPostRequestBody verifyTokenPostRequestBody) {
        passwordResetService.tokenIsValid(verifyTokenPostRequestBody.getToken(), verifyTokenPostRequestBody.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmPasswordReset(@Valid @RequestBody NewPasswordPostRequestBody newPasswordPostRequestBody) {
        passwordResetService.resetPassword(newPasswordPostRequestBody);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
