package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.requests.NewPasswordPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.RequestPasswordReset;
import com.github.skyg0d.skydrinksapi.requests.VerifyTokenPostRequestBody;
import com.github.skyg0d.skydrinksapi.service.PasswordResetService;
import com.github.skyg0d.skydrinksapi.util.password.NewPasswordPostRequestBodyCreator;
import com.github.skyg0d.skydrinksapi.util.password.RequestPasswordResetCreator;
import com.github.skyg0d.skydrinksapi.util.password.VerifyTokenPostRequestBodyCretor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for PasswordResetController")
public class PasswordResetControllerTest {

    @InjectMocks
    private PasswordResetController passwordResetController;

    @Mock
    private PasswordResetService passwordResetServiceMock;

    @Test
    @DisplayName("requestPasswordReset persists password reset when successful")
    void requestPasswordReset_PersistsPasswordReset_WhenSuccessful() {
        RequestPasswordReset requestPasswordReset = RequestPasswordResetCreator.createRequestPasswordReset();

        ResponseEntity<Void> entity = passwordResetController.requestPasswordReset(requestPasswordReset);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("tokenIsValid returns password reset when successful")
    void tokenIsValid_ReturnsPasswordReset_WhenSuccessful() {
        VerifyTokenPostRequestBody verifyTokenPostRequestBody = VerifyTokenPostRequestBodyCretor.createVerifyTokenPostRequestBody();

        ResponseEntity<Void> entity = passwordResetController.verifyToken(verifyTokenPostRequestBody);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("resetPassword updates an user password when successful")
    void resetPassword_UpdatesAnUserPassword_WhenSuccessful() {
        NewPasswordPostRequestBody passwordResetToBeSave = NewPasswordPostRequestBodyCreator.createPasswordResetToBeSave();

        ResponseEntity<Void> entity = passwordResetController.confirmPasswordReset(passwordResetToBeSave);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);
    }

}
