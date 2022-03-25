package com.github.skyg0d.skydrinksapi.repository;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.domain.TotalUsers;
import com.github.skyg0d.skydrinksapi.repository.user.ApplicationUserRepository;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
@DisplayName("Tests for ApplicationUserRepository")
class ApplicationUserRepositoryTest {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Test
    @DisplayName("countTotalUsers returns total of users when successful")
    void countTotalUsers_ReturnsTotalOfUsers_WhenSuccessful() {
        applicationUserRepository.save(ApplicationUserCreator.createAdminApplicationUser());

        applicationUserRepository.save(ApplicationUserCreator.createApplicationUserWithRequestsLocked());

        TotalUsers totalUsers = applicationUserRepository.countTotalUsers();

        assertThat(totalUsers).isNotNull();

        assertThat(totalUsers.getTotal())
                .isNotNull()
                .isEqualTo(2);

        assertThat(totalUsers.getUnlocked())
                .isNotNull()
                .isEqualTo(1);

        assertThat(totalUsers.getLocked())
                .isNotNull()
                .isEqualTo(1);
    }

    @Test
    @DisplayName("save persist application user when successful")
    void save_PersistApplicationUser_WhenSuccessful() {
        ApplicationUser applicationUserToBeSave = ApplicationUserCreator.createApplicationUserToBeSave();

        ApplicationUser applicationUserSaved = applicationUserRepository.save(applicationUserToBeSave);

        assertThat(applicationUserSaved).isNotNull();

        assertThat(applicationUserSaved.getUuid()).isNotNull();

        assertThat(applicationUserSaved.getName())
                .isNotNull()
                .isEqualTo(applicationUserToBeSave.getName());

        assertThat(applicationUserSaved.getEmail())
                .isNotNull()
                .isEqualTo(applicationUserToBeSave.getEmail());

        assertThat(applicationUserSaved.getPassword())
                .isNotNull()
                .isEqualTo(applicationUserToBeSave.getPassword());

        assertThat(applicationUserSaved.getRole())
                .isNotNull()
                .isEqualTo(applicationUserToBeSave.getRole());
    }

    @Test
    @DisplayName("save updates application user when successful")
    void save_UpdatesApplicationUser_WhenSuccessful() {
        ApplicationUser applicationUserToBeSave = ApplicationUserCreator.createApplicationUserToBeSave();

        ApplicationUser applicationUserSaved = applicationUserRepository.save(applicationUserToBeSave);

        applicationUserSaved.setRole("ADMIN");

        ApplicationUser applicationUserUpdated = applicationUserRepository.save(applicationUserSaved);

        assertThat(applicationUserUpdated).isNotNull();

        assertThat(applicationUserUpdated.getUuid()).isNotNull();

        assertThat(applicationUserUpdated.getName())
                .isNotNull()
                .isEqualTo(applicationUserSaved.getName());

        assertThat(applicationUserUpdated.getEmail())
                .isNotNull()
                .isEqualTo(applicationUserSaved.getEmail());

        assertThat(applicationUserUpdated.getPassword())
                .isNotNull()
                .isEqualTo(applicationUserSaved.getPassword());

        assertThat(applicationUserUpdated.getRole())
                .isNotNull()
                .isEqualTo(applicationUserSaved.getRole());
    }

    @Test
    @DisplayName("delete removes application user when successful")
    void delete_RemovesApplicationUserWhenSuccessful() {
        ApplicationUser applicationUserToBeSave = ApplicationUserCreator.createApplicationUserToBeSave();

        ApplicationUser applicationUserSaved = applicationUserRepository.save(applicationUserToBeSave);

        applicationUserRepository.delete(applicationUserSaved);

        Optional<ApplicationUser> applicationUserFound = applicationUserRepository.findById(applicationUserSaved.getUuid());

        assertThat(applicationUserFound).isEmpty();
    }

    @Test
    @DisplayName("save throws ConstraintViolationException when application user request properties is invalid")
    void save_ThrowsConstraintViolationException_WhenApplicationUserPropertiesIsInvalid() {
        ApplicationUser applicationUser = new ApplicationUser();

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> applicationUserRepository.saveAndFlush(applicationUser));

    }

}
