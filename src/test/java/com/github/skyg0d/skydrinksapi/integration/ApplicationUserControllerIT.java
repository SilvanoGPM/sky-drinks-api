package com.github.skyg0d.skydrinksapi.integration;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.repository.user.ApplicationUserRepository;
import com.github.skyg0d.skydrinksapi.requests.ApplicationUserPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.ApplicationUserPutRequestBody;
import com.github.skyg0d.skydrinksapi.util.TokenUtil;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserCreator;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserPostRequestBodyCreator;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserPutRequestBodyCreator;
import com.github.skyg0d.skydrinksapi.wrapper.PageableResponse;
import lombok.extern.log4j.Log4j2;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Integration Tests for ApplicationUserController")
@Log4j2
class ApplicationUserControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private TokenUtil tokenUtil;

    @Test
    @DisplayName("getUserInfo return application user when successful")
    void getUserInfo_ReturnApplicationUser_WhenSuccessful() {
        ApplicationUser expectedApplicationUser = ApplicationUserCreator.createAdminApplicationUser();

        ResponseEntity<ApplicationUser> entity = testRestTemplate.exchange(
                "/users/all/user-info",
                HttpMethod.GET,
                tokenUtil.createAdminAuthEntity(null),
                ApplicationUser.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getEmail())
                .isNotNull()
                .isEqualTo(expectedApplicationUser.getEmail());

        assertThat(entity.getBody().getCpf())
                .isNotNull()
                .isEqualTo(expectedApplicationUser.getCpf());

        assertThat(entity.getBody().getName())
                .isNotNull()
                .isEqualTo(expectedApplicationUser.getName());
    }

    @Test
    @DisplayName("listAll return list of application users inside page object when successful")
    void listAll_ReturnListOfApplicationUsersInsidePageObject_WhenSuccessful() {
        ApplicationUser expectedApplicationUser = applicationUserRepository.save(ApplicationUserCreator.createApplicationUserToBeSave());

        ResponseEntity<PageableResponse<ApplicationUser>> entity = testRestTemplate.exchange(
                "/users/admin?size=20",
                HttpMethod.GET,
                tokenUtil.createAdminAuthEntity(null),
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isNotEmpty()
                .contains(expectedApplicationUser);
    }

    @Test
    @DisplayName("search return list of application users inside page object when successful")
    void search_ReturnListOfApplicationUsersInsidePageObject_WhenSuccessful() {
        ApplicationUser expectedApplicationUser = applicationUserRepository.save(ApplicationUserCreator.createApplicationUserToBeSave());

        String url = String.format("/users/admin/search?name=%s&size=20", expectedApplicationUser.getName());

        ResponseEntity<PageableResponse<ApplicationUser>> entity = testRestTemplate.exchange(
                url,
                HttpMethod.GET,
                tokenUtil.createAdminAuthEntity(null),
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isNotEmpty()
                .contains(expectedApplicationUser);
    }

    @Test
    @DisplayName("search return empty page object when does not match")
    void search_ReturnEmptyPage_WhenDoesNotMatch() {
        String url = String.format("/users/admin/search?name=%s", "qfqqg");

        ResponseEntity<PageableResponse<ApplicationUser>> entity = testRestTemplate.exchange(
                url,
                HttpMethod.GET,
                tokenUtil.createAdminAuthEntity(null),
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isEmpty();
    }

    @Test
    @DisplayName("findById return application user when successful")
    void findById_ReturnApplicationUser_WhenSuccessful() {
        ApplicationUser expectedApplicationUser = applicationUserRepository.save(ApplicationUserCreator.createApplicationUserToBeSave());

        ResponseEntity<ApplicationUser> entity = testRestTemplate.exchange(
                "/users/all/{uuid}",
                HttpMethod.GET,
                tokenUtil.createAdminAuthEntity(null),
                ApplicationUser.class,
                expectedApplicationUser.getUuid()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedApplicationUser);
    }

    @Test
    @DisplayName("findById returns 400 BadRequest when application user not exists")
    void findById_Returns400BadRequest_WhenApplicationUserNotExists() {
        ResponseEntity<ApplicationUser> entity = testRestTemplate.exchange(
                "/users/all/{uuid}",
                HttpMethod.GET,
                tokenUtil.createAdminAuthEntity(null),
                ApplicationUser.class,
                UUID.randomUUID()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("findByIdEmail return application user when successful")
    void findByEmail_ReturnApplicationUser_WhenSuccessful() {
        ApplicationUser expectedApplicationUser = applicationUserRepository.save(ApplicationUserCreator.createApplicationUserToBeSave());

        ResponseEntity<ApplicationUser> entity = testRestTemplate.exchange(
                "/users/admin/find-by-email/{email}",
                HttpMethod.GET,
                tokenUtil.createAdminAuthEntity(null),
                ApplicationUser.class,
                expectedApplicationUser.getEmail()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedApplicationUser);
    }

    @Test
    @DisplayName("findByIdEmail returns 400 BadRequest when application user not exists")
    void findByEmail_Returns400BadRequest_WhenApplicationUserNotExists() {
        ResponseEntity<ApplicationUser> entity = testRestTemplate.exchange(
                "/users/admin/find-by-email/{email}",
                HttpMethod.GET,
                tokenUtil.createAdminAuthEntity(null),
                ApplicationUser.class,
                UUID.randomUUID()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("findByCpf return application user when successful")
    void findByCpf_ReturnApplicationUser_WhenSuccessful() {
        ApplicationUser expectedApplicationUser = applicationUserRepository.save(ApplicationUserCreator.createApplicationUserToBeSave());

        ResponseEntity<ApplicationUser> entity = testRestTemplate.exchange(
                "/users/admin/find-by-cpf/{cpf}",
                HttpMethod.GET,
                tokenUtil.createAdminAuthEntity(null),
                ApplicationUser.class,
                expectedApplicationUser.getCpf()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedApplicationUser);
    }

    @Test
    @DisplayName("findByCpf returns 400 BadRequest when application user not exists")
    void findByCpf_Returns400BadRequest_WhenApplicationUserNotExists() {
        ResponseEntity<ApplicationUser> entity = testRestTemplate.exchange(
                "/users/admin/find-by-cpf/{cpf}",
                HttpMethod.GET,
                tokenUtil.createAdminAuthEntity(null),
                ApplicationUser.class,
                UUID.randomUUID()
        );


        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("save creates application user when successful")
    void save_CreatesApplicationUser_WhenSuccessful() {
        ApplicationUserPostRequestBody userValid = ApplicationUserPostRequestBodyCreator.createApplicationUserPostRequestBodyToBeSave();

        ResponseEntity<ApplicationUser> entity = testRestTemplate.exchange(
                "/users/admin",
                HttpMethod.POST,
                tokenUtil.createAdminAuthEntity(userValid),
                ApplicationUser.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getUuid()).isNotNull();

        assertThat(entity.getBody().getName())
                .isNotNull()
                .isEqualTo(userValid.getName());

        assertThat(entity.getBody().getEmail())
                .isNotNull()
                .isEqualTo(userValid.getEmail());

        assertThat(entity.getBody().getRole())
                .isNotNull()
                .isEqualTo(userValid.getRole());
    }

    @Test
    @DisplayName("save returns 403 Forbidden when user does not have ROLE_ADMIN")
    void save_Returns403Forbidden_WhenUserDoesNotHaveROLE_ADMIN() {
        ApplicationUserPostRequestBody userValid = ApplicationUserPostRequestBodyCreator.createApplicationUserPostRequestBodyToBeSave();

        ResponseEntity<ApplicationUser> entity = testRestTemplate.exchange(
                "/users/admin",
                HttpMethod.POST,
                tokenUtil.createUserAuthEntity(userValid),
                ApplicationUser.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("replace updates application user when successful")
    void replace_UpdatedApplicationUser_WhenSuccessful() {
        ApplicationUser userSaved = applicationUserRepository.save(ApplicationUserCreator.createApplicationUserToBeSave());

        ApplicationUserPutRequestBody userToUpdate = ApplicationUserPutRequestBodyCreator.createApplicationUserPutRequestBodyToBeSave();

        userToUpdate.setUuid(userSaved.getUuid());

        ResponseEntity<Void> entity = testRestTemplate.exchange(
                "/users/user",
                HttpMethod.PUT,
                tokenUtil.createAdminAuthEntity(userToUpdate),
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("replace returns 403 Forbidden when user does not have ROLE_USER")
    void replace_Returns403Forbidden_WhenUserDoesNotHaveROLE_USER() {
        ApplicationUser userSaved = applicationUserRepository.save(ApplicationUserCreator.createApplicationUserToBeSave());

        ApplicationUserPutRequestBody userToUpdate = ApplicationUserPutRequestBodyCreator.createApplicationUserPutRequestBodyToBeSave();

        userToUpdate.setUuid(userSaved.getUuid());

        ResponseEntity<Void> entity = testRestTemplate.exchange(
                "/users/user",
                HttpMethod.PUT,
                tokenUtil.createUserAuthEntity(userToUpdate),
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("replace returns 403 Forbidden when user does not have permission to modify user")
    void replace_Returns403Forbidden_WhenUserDoesNotHavePermissionToModifyUser() {
        ApplicationUser userSaved = applicationUserRepository.save(ApplicationUserCreator.createApplicationUserToBeSave());

        ApplicationUserPutRequestBody userToUpdate = ApplicationUserPutRequestBodyCreator.createApplicationUserPutRequestBodyToBeSave();

        userToUpdate.setUuid(userSaved.getUuid());

        ResponseEntity<Void> entity = testRestTemplate.exchange(
                "/users/user",
                HttpMethod.PUT,
                tokenUtil.createBarmenAuthEntity(userToUpdate),
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("toggleLockRequests lock user requests when user requests is unlocked")
    void toggleLockRequests_LockUserRequests_WhenUserRequestsIsUnlocked() {
        ApplicationUser userSaved = applicationUserRepository.save(ApplicationUserCreator.createApplicationUserToBeSave());

        ResponseEntity<ApplicationUser> entity = testRestTemplate.exchange(
                "/users/admin/toggle-lock-requests/{uuid}",
                HttpMethod.PATCH,
                tokenUtil.createAdminAuthEntity(null),
                ApplicationUser.class,
                userSaved.getUuid()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(userSaved);

        assertThat(entity.getBody().isLockRequests()).isNotEqualTo(userSaved.isLockRequests());
    }

    @Test
    @DisplayName("toggleLockRequests unlock user requests when user requests is locked")
    void toggleLockRequests_UnlockUserRequests_WhenUserRequestsIsLocked() {
        ApplicationUser userSaved = applicationUserRepository.save(ApplicationUserCreator.createApplicationUserWithRequestsLocked());

        ResponseEntity<ApplicationUser> entity = testRestTemplate.exchange(
                "/users/admin/toggle-lock-requests/{uuid}",
                HttpMethod.PATCH,
                tokenUtil.createAdminAuthEntity(null),
                ApplicationUser.class,
                userSaved.getUuid()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(userSaved);

        assertThat(entity.getBody().isLockRequests()).isNotEqualTo(userSaved.isLockRequests());
    }

    @Test
    @DisplayName("delete removes application user when successful")
    void delete_RemovesApplicationUser_WhenSuccessful() {
        ApplicationUser userSaved = applicationUserRepository.save(ApplicationUserCreator.createApplicationUserToBeSave());

        ResponseEntity<Void> entity = testRestTemplate.exchange(
                "/users/user/{uuid}",
                HttpMethod.DELETE,
                tokenUtil.createAdminAuthEntity(null),
                Void.class,
                userSaved.getUuid()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete returns 403 Forbidden when user does not have permission to modify user")
    void delete_Returns403Forbidden_WhenUserDoesNotHavePermissionToModifyUser() {
        ApplicationUser userSaved = applicationUserRepository.save(ApplicationUserCreator.createApplicationUserToBeSave());

        ResponseEntity<Void> entity = testRestTemplate.exchange(
                "/users/user/{uuid}",
                HttpMethod.DELETE,
                tokenUtil.createUserAuthEntity(null),
                Void.class,
                userSaved.getUuid()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("delete returns 403 Forbidden when user does not have ROLE_USER")
    void delete_Returns403Forbidden_WhenUserDoesNotHaveROLE_USER() {
        ApplicationUser userSaved = applicationUserRepository.save(ApplicationUserCreator.createApplicationUserToBeSave());

        ResponseEntity<Void> entity = testRestTemplate.exchange(
                "/users/user/{uuid}",
                HttpMethod.DELETE,
                tokenUtil.createBarmenAuthEntity(null),
                Void.class,
                userSaved.getUuid()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

}
