package com.github.skyg0d.skydrinksapi.integration;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.domain.PasswordReset;
import com.github.skyg0d.skydrinksapi.repository.password.PasswordResetRepository;
import com.github.skyg0d.skydrinksapi.repository.user.ApplicationUserRepository;
import com.github.skyg0d.skydrinksapi.requests.NewPasswordPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.RequestPasswordReset;
import com.github.skyg0d.skydrinksapi.requests.VerifyTokenPostRequestBody;
import com.github.skyg0d.skydrinksapi.util.TokenUtil;
import com.github.skyg0d.skydrinksapi.util.password.NewPasswordPostRequestBodyCreator;
import com.github.skyg0d.skydrinksapi.util.password.PasswordResetCreator;
import com.github.skyg0d.skydrinksapi.util.password.RequestPasswordResetCreator;
import com.github.skyg0d.skydrinksapi.util.password.VerifyTokenPostRequestBodyCretor;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Integration Tests for PasswordResetController")
public class PasswordResetControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Test
    @DisplayName("requestPasswordReset persists password reset when successful")
    void requestPasswordReset_PersistsPasswordReset_WhenSuccessful() {
        ApplicationUser userSaved = applicationUserRepository.save(ApplicationUserCreator.createValidApplicationUser());

        ResponseEntity<Void> entity = testRestTemplate.postForEntity(
                "/password-reset/request",
                RequestPasswordResetCreator.createRequestPasswordReset(),
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        List<PasswordReset> passwordResetsFound = passwordResetRepository.findAll();

        assertThat(passwordResetsFound)
                .isNotEmpty()
                .hasSize(1);

        assertThat(passwordResetsFound.get(0)).isNotNull();

        assertThat(passwordResetsFound.get(0).getUser())
                .isNotNull()
                .isEqualTo(userSaved);
    }

    @Test
    @DisplayName("verifyToken returns password reset when successful")
    void tokenIsValid_ReturnsPasswordReset_WhenSuccessful() {
        ApplicationUser userSaved = applicationUserRepository.save(ApplicationUserCreator.createValidApplicationUser());

        PasswordReset passwordResetToBeSave = PasswordResetCreator.createPasswordResetToBeSave();

        passwordResetToBeSave.setUser(userSaved);

        passwordResetToBeSave.setExpireDate(LocalDateTime.now().plusMinutes(30));

        PasswordReset passwordResetSaved = passwordResetRepository.save(passwordResetToBeSave);

        VerifyTokenPostRequestBody verifyTokenPostRequestBody = VerifyTokenPostRequestBodyCretor.createVerifyTokenPostRequestBody();

        verifyTokenPostRequestBody.setEmail(userSaved.getEmail());

        verifyTokenPostRequestBody.setToken(passwordResetSaved.getToken());

        ResponseEntity<Void> entity = testRestTemplate.postForEntity(
                "/password-reset/verify",
                verifyTokenPostRequestBody,
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("confirmPasswordReset updates an user password when successful")
    void confirmPasswordReset_UpdatesAnUserPassword_WhenSuccessful() {
        ApplicationUser userSaved = applicationUserRepository.save(ApplicationUserCreator.createValidApplicationUser());

        PasswordReset passwordResetToBeSave = PasswordResetCreator.createPasswordResetToBeSave();

        passwordResetToBeSave.setUser(userSaved);

        passwordResetToBeSave.setExpireDate(LocalDateTime.now().plusMinutes(30));

        PasswordReset passwordResetSaved = passwordResetRepository.save(passwordResetToBeSave);

        NewPasswordPostRequestBody newPasswordResetToBeSave = NewPasswordPostRequestBodyCreator.createPasswordResetToBeSave();

        newPasswordResetToBeSave.setEmail(userSaved.getEmail());

        newPasswordResetToBeSave.setToken(passwordResetSaved.getToken());

        ResponseEntity<Void> entity =  testRestTemplate.postForEntity(
                "/password-reset/confirm",
                newPasswordResetToBeSave,
                Void.class
        );
        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        List<PasswordReset> passwordResetsFound = passwordResetRepository.findAll();

        assertThat(passwordResetsFound)
                .isNotEmpty()
                .hasSize(1);

        assertThat(passwordResetsFound.get(0)).isNotNull();

        assertThat(passwordResetsFound.get(0).isResetFinished()).isTrue();
    }

    @Test
    @DisplayName("requestPasswordReset returns 400 BadRequest when user not found")
    void requestPasswordReset_Returns400BadRequest_WhenUserIsNotFound() {
        ResponseEntity<Void> entity = testRestTemplate.postForEntity(
                "/password-reset/request",
                RequestPasswordResetCreator.createRequestPasswordReset(),
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("verifyToken returns 400 BadRequest when token expired")
    void tokenIsValid_Returns400BadRequest_WhenTokenExpired() {
        ApplicationUser userSaved = applicationUserRepository.save(ApplicationUserCreator.createValidApplicationUser());

        PasswordReset passwordResetToBeSave = PasswordResetCreator.createPasswordResetToBeSave();

        passwordResetToBeSave.setUser(userSaved);

        PasswordReset passwordResetSaved = passwordResetRepository.save(passwordResetToBeSave);

        VerifyTokenPostRequestBody verifyTokenPostRequestBody = VerifyTokenPostRequestBodyCretor.createVerifyTokenPostRequestBody();

        verifyTokenPostRequestBody.setEmail(userSaved.getEmail());
        verifyTokenPostRequestBody.setToken(passwordResetSaved.getToken());

        ResponseEntity<Void> entity = testRestTemplate.postForEntity(
                "/password-reset/verify",
                verifyTokenPostRequestBody,
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("verifyToken returns 400 BadRequest when is invalid")
    void tokenIsValid_Returns400BadRequest_WhenTokenIsInvalid() {
        ApplicationUser userSaved = applicationUserRepository.save(ApplicationUserCreator.createValidApplicationUser());

        PasswordReset passwordResetToBeSave = PasswordResetCreator.createPasswordResetToBeSave();

        passwordResetToBeSave.setUser(userSaved);

        passwordResetRepository.save(passwordResetToBeSave);

        VerifyTokenPostRequestBody verifyTokenPostRequestBody = VerifyTokenPostRequestBodyCretor.createVerifyTokenPostRequestBody();

        verifyTokenPostRequestBody.setEmail(userSaved.getEmail());

        ResponseEntity<Void> entity = testRestTemplate.postForEntity(
                "/password-reset/verify",
                verifyTokenPostRequestBody,
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("verifyToken returns 400 BadRequest when user is not found")
    void tokenIsValid_Returns400BadRequest_WhenUserIsNotFound() {
        VerifyTokenPostRequestBody verifyTokenPostRequestBody = VerifyTokenPostRequestBodyCretor.createVerifyTokenPostRequestBody();

        ResponseEntity<Void> entity = testRestTemplate.postForEntity(
                "/password-reset/verify",
                verifyTokenPostRequestBody,
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

}
