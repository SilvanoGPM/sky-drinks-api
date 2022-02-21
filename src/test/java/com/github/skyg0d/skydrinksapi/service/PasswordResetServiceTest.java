package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.domain.PasswordReset;
import com.github.skyg0d.skydrinksapi.exception.BadRequestException;
import com.github.skyg0d.skydrinksapi.property.PasswordResetProperties;
import com.github.skyg0d.skydrinksapi.repository.password.PasswordResetRepository;
import com.github.skyg0d.skydrinksapi.repository.user.ApplicationUserRepository;
import com.github.skyg0d.skydrinksapi.requests.NewPasswordPostRequestBody;
import com.github.skyg0d.skydrinksapi.util.password.NewPasswordPostRequestBodyCreator;
import com.github.skyg0d.skydrinksapi.util.password.PasswordResetCreator;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.mail.internet.MimeMessage;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for PasswordResetService")
public class PasswordResetServiceTest {

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Mock
    private ApplicationUserService applicationUserServiceMock;

    @Mock
    private ApplicationUserRepository applicationUserRepositoryMock;

    @Mock
    private PasswordResetRepository passwordResetRepositoryMock;

    @Mock
    private PasswordResetProperties passwordResetPropertiesMock;

    @Mock
    private JavaMailSender javaMailSenderMock;

    @BeforeEach
    void setUp() {
        PasswordReset passwordReset = PasswordResetCreator.createValidPasswordReset();
        passwordReset.setExpireDate(LocalDateTime.now().plusMinutes(30));

        BDDMockito
                .when(applicationUserServiceMock.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(ApplicationUserCreator.createValidApplicationUser());

        BDDMockito
                .when(passwordResetRepositoryMock.findByUser(ArgumentMatchers.any(ApplicationUser.class)))
                .thenReturn(List.of(passwordReset));

        BDDMockito
                .when(passwordResetPropertiesMock.getTokenLength())
                .thenReturn(10);

        BDDMockito
                .when(passwordResetPropertiesMock.getExpireMinutes())
                .thenReturn(30);

        BDDMockito
                .when(javaMailSenderMock.createMimeMessage())
                .thenReturn(Mockito.mock(MimeMessage.class));
    }

    @Test
    @DisplayName("create persists password reset when successful")
    void create_PersistsPasswordReset_WhenSuccessful() {
        String email = ApplicationUserCreator.createValidApplicationUser().getEmail();

        assertThatCode(() -> passwordResetService.create(email))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("tokenIsValid returns password reset when successful")
    void tokenIsValid_ReturnsPasswordReset_WhenSuccessful() {
        String email = ApplicationUserCreator.createValidApplicationUser().getEmail();

        PasswordReset passwordResetValid = PasswordResetCreator.createValidPasswordReset();

        String token = passwordResetValid.getToken();

        PasswordReset passwordResetFound = passwordResetService.tokenIsValid(token, email);

        assertThat(passwordResetFound)
                .isNotNull()
                .isEqualTo(passwordResetValid);
    }

    @Test
    @DisplayName("resetPassword updates an user password when successful")
    void resetPassword_UpdatesAnUserPassword_WhenSuccessful() {
        NewPasswordPostRequestBody passwordResetToBeSave = NewPasswordPostRequestBodyCreator.createPasswordResetToBeSave();

        assertThatCode(() -> passwordResetService.resetPassword(passwordResetToBeSave))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("tokenIsValid throws BadRequestException when token is invalid")
    void tokenIsValid_ThrowsBadRequestException_WhenTokenIsInvalid() {
        String email = ApplicationUserCreator.createValidApplicationUser().getEmail();

       assertThatExceptionOfType(BadRequestException.class)
               .isThrownBy(() -> passwordResetService.tokenIsValid("jqf", email));
    }

    @Test
    @DisplayName("tokenIsValid throws BadRequestException when token expired")
    void tokenIsValid_ThrowsBadRequestException_WhenTokenExpired() {
        PasswordReset passwordResetValid = PasswordResetCreator.createValidPasswordReset();

        BDDMockito
                .when(passwordResetRepositoryMock.findByUser(ArgumentMatchers.any(ApplicationUser.class)))
                .thenReturn(List.of(passwordResetValid));

        String email = ApplicationUserCreator.createValidApplicationUser().getEmail();

        String token = passwordResetValid.getToken();

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> passwordResetService.tokenIsValid(token, email));
    }

}
