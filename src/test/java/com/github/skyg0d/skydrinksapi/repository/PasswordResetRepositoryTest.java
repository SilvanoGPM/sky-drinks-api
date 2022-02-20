package com.github.skyg0d.skydrinksapi.repository;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.domain.PasswordReset;
import com.github.skyg0d.skydrinksapi.repository.password.PasswordResetRepository;
import com.github.skyg0d.skydrinksapi.repository.user.ApplicationUserRepository;
import com.github.skyg0d.skydrinksapi.util.password.PasswordResetCreator;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
@DisplayName("Tests for TableRepository")
class PasswordResetRepositoryTest {

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Test
    @DisplayName("save persist password reset when successful")
    void save_PersistPasswordReset_WhenSuccessful() {
        PasswordReset passwordResetToBeSave = PasswordResetCreator.createPasswordResetToBeSave();

        ApplicationUser userSaved = applicationUserRepository.save(ApplicationUserCreator.createApplicationUserToBeSave());

        passwordResetToBeSave.setUser(userSaved);

        PasswordReset passwordResetSaved = passwordResetRepository.save(passwordResetToBeSave);

        assertThat(passwordResetSaved).isNotNull();

        assertThat(passwordResetSaved.getToken())
                .isNotNull()
                .isEqualTo(passwordResetToBeSave.getToken());
    }

    @Test
    @DisplayName("save updates password reset when successful")
    void save_UpdatesPasswordReset_WhenSuccessful() {
        PasswordReset passwordResetToBeSave = PasswordResetCreator.createPasswordResetToBeSave();

        ApplicationUser userSaved = applicationUserRepository.save(ApplicationUserCreator.createApplicationUserToBeSave());

        passwordResetToBeSave.setUser(userSaved);

        PasswordReset passwordResetSaved = passwordResetRepository.save(passwordResetToBeSave);

        passwordResetSaved.setResetFinished(true);

        PasswordReset passwordResetUpdated = passwordResetRepository.save(passwordResetSaved);

        assertThat(passwordResetUpdated).isNotNull();

        assertThat(passwordResetUpdated.getToken())
                .isNotNull()
                .isEqualTo(passwordResetToBeSave.getToken());

        assertThat(passwordResetUpdated.isResetFinished()).isTrue();
    }

    @Test
    @DisplayName("delete removes password reset when successful")
    void delete_RemovesPasswordReset_WhenSuccessful() {
        PasswordReset passwordResetToBeSave = PasswordResetCreator.createPasswordResetToBeSave();

        ApplicationUser userSaved = applicationUserRepository.save(ApplicationUserCreator.createApplicationUserToBeSave());

        passwordResetToBeSave.setUser(userSaved);

        PasswordReset passwordResetSaved = passwordResetRepository.save(passwordResetToBeSave);

        passwordResetRepository.delete(passwordResetSaved);

        Optional<PasswordReset> passwordResetFound = passwordResetRepository.findById(userSaved.getUuid());

        assertThat(passwordResetFound).isEmpty();
    }

    @Test
    @DisplayName("findByUser returns password reset when successful")
    void findByUser_ReturnsPasswordReset_WhenSuccessful() {
        PasswordReset passwordResetToBeSave = PasswordResetCreator.createPasswordResetToBeSave();

        ApplicationUser userSaved = applicationUserRepository.save(ApplicationUserCreator.createApplicationUserToBeSave());

        passwordResetToBeSave.setUser(userSaved);

        PasswordReset passwordResetSaved = passwordResetRepository.save(passwordResetToBeSave);

        List<PasswordReset> passwordResetsFound = passwordResetRepository.findByUser(userSaved);

        assertThat(passwordResetsFound)
                .isNotEmpty()
                .hasSize(1);

        assertThat(passwordResetsFound.get(0)).isEqualTo(passwordResetSaved);
    }

    @Test
    @DisplayName("save throws ConstraintViolationException when passwordReset properties is invalid")
    void save_ThrowsConstraintViolationException_WhenPasswordResetPropertiesIsInvalid() {
        PasswordReset passwordReset = new PasswordReset();

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> passwordResetRepository.saveAndFlush(passwordReset));

    }

}