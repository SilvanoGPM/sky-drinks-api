package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.parameters.ApplicationUserParameters;
import com.github.skyg0d.skydrinksapi.requests.ApplicationUserPostRequestBody;
import com.github.skyg0d.skydrinksapi.service.ApplicationUserService;
import com.github.skyg0d.skydrinksapi.util.AuthUtil;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserCreator;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserPostRequestBodyCreator;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserPutRequestBodyCreator;
import com.github.skyg0d.skydrinksapi.util.user.LoginPostRequestBodyCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for ApplicationUserController")
class ApplicationUserControllerTest {

    @InjectMocks
    private ApplicationUserController applicationUserController;

    @Mock
    private ApplicationUserService applicationUserServiceMock;

    @Mock
    private AuthUtil authUtilMock;

    @BeforeEach
    void setUp() {
        PageImpl<ApplicationUser> applicationUsersPage = new PageImpl<>(List.of(ApplicationUserCreator.createValidApplicationUser()));

        BDDMockito
                .when(authUtilMock.getUser(ArgumentMatchers.any(Principal.class)))
                .thenReturn(ApplicationUserCreator.createValidApplicationUser());

        BDDMockito
                .when(applicationUserServiceMock.listAll(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(applicationUsersPage);

        BDDMockito
                .when(applicationUserServiceMock.findByIdOrElseThrowBadRequestException(ArgumentMatchers.any(UUID.class)))
                .thenReturn(ApplicationUserCreator.createValidApplicationUser());

        BDDMockito
                .when(applicationUserServiceMock.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(ApplicationUserCreator.createValidApplicationUser());

        BDDMockito
                .when(applicationUserServiceMock.findByCpf(ArgumentMatchers.anyString()))
                .thenReturn(ApplicationUserCreator.createValidApplicationUser());

        BDDMockito
                .when(applicationUserServiceMock.search(ArgumentMatchers.any(ApplicationUserParameters.class), ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(applicationUsersPage);

        BDDMockito
                .when(applicationUserServiceMock.save(ArgumentMatchers.any(ApplicationUserPostRequestBody.class)))
                .thenReturn(ApplicationUserCreator.createValidApplicationUser());

        BDDMockito
                .when(applicationUserServiceMock.toggleLockRequests(ArgumentMatchers.any(UUID.class)))
                .thenReturn(ApplicationUserCreator.createApplicationUserWithRequestsLocked());

        BDDMockito
                .doNothing()
                .when(applicationUserServiceMock)
                .delete(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(ApplicationUser.class));
    }

    @Test
    @DisplayName("getUserInfo return application user when successful")
    void getUserInfo_ReturnApplicationUser_WhenSuccessful() {
        ApplicationUser expectedApplicationUser = ApplicationUserCreator.createValidApplicationUser();

        Principal principalMock = Mockito.mock(Principal.class);

        ResponseEntity<ApplicationUser> entity = applicationUserController.getUserInfo(principalMock);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedApplicationUser);
    }

    @Test
    @DisplayName("listAll return list of application users inside page object when successful")
    void listAll_ReturnListOfApplicationUsersInsidePageObject_WhenSuccessful() {
        ApplicationUser expectedApplicationUser = ApplicationUserCreator.createValidApplicationUser();

        ResponseEntity<Page<ApplicationUser>> entity = applicationUserController.listAll(PageRequest.of(1, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedApplicationUser);
    }

    @Test
    @DisplayName("listAll return empty page when there are no application users")
    void listAll_ReturnEmptyPage_WhenThereAreNoApplicationUsers() {
        BDDMockito
                .when(applicationUserServiceMock.listAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(Page.empty());

        ResponseEntity<Page<ApplicationUser>> entity = applicationUserController.listAll(PageRequest.of(1, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isEmpty();
    }

    @Test
    @DisplayName("search return list of application users inside page object when successful")
    void search_ReturnListOfApplicationUsersInsidePageObject_WhenSuccessful() {
        ApplicationUser expectedApplicationUser = ApplicationUserCreator.createValidApplicationUser();

        ResponseEntity<Page<ApplicationUser>> entity = applicationUserController.search(new ApplicationUserParameters(), PageRequest.of(1, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedApplicationUser);
    }

    @Test
    @DisplayName("findById return application user when successful")
    void findById_ReturnApplicationUser_WhenSuccessful() {
        ApplicationUser expectedApplicationUser = ApplicationUserCreator.createValidApplicationUser();

        ResponseEntity<ApplicationUser> entity = applicationUserController.findById(UUID.randomUUID());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedApplicationUser);
    }

    @Test
    @DisplayName("findByIdEmail return application user when successful")
    void findByEmail_ReturnApplicationUser_WhenSuccessful() {
        ApplicationUser expectedApplicationUser = ApplicationUserCreator.createValidApplicationUser();

        ResponseEntity<ApplicationUser> entity = applicationUserController.findByEmail(expectedApplicationUser.getEmail());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedApplicationUser);
    }

    @Test
    @DisplayName("findByCpf return application user when successful")
    void findByCpf_ReturnApplicationUser_WhenSuccessful() {
        ApplicationUser expectedApplicationUser = ApplicationUserCreator.createValidApplicationUser();

        ResponseEntity<ApplicationUser> entity = applicationUserController.findByCpf(expectedApplicationUser.getCpf());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedApplicationUser);
    }

    @Test
    @DisplayName("save creates application user when successful")
    void save_CreatesApplicationUser_WhenSuccessful() {
        ApplicationUser expectedApplicationUser = ApplicationUserCreator.createValidApplicationUser();

        ResponseEntity<ApplicationUser> entity = applicationUserController.save(ApplicationUserPostRequestBodyCreator.createApplicationUserPostRequestBodyToBeSave());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedApplicationUser);
    }

    @Test
    @DisplayName("replace updates application user when successful")
    void replace_UpdatedApplicationUser_WhenSuccessful() {
        Principal principalMock = Mockito.mock(Principal.class);

        ResponseEntity<Void> entity = applicationUserController.replace(ApplicationUserPutRequestBodyCreator.createApplicationUserPutRequestBodyToBeSave(), principalMock);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("toggleLockRequests lock user requests when user requests is unlocked")
    void toggleLockRequests_LockUserRequests_WhenUserRequestsIsUnlocked() {
        ApplicationUser expectedApplicationUser = ApplicationUserCreator.createValidApplicationUser();

        ResponseEntity<ApplicationUser> entity = applicationUserController.toggleLockRequests(expectedApplicationUser.getUuid());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedApplicationUser);

        assertThat(entity.getBody().isLockRequests()).isNotEqualTo(expectedApplicationUser.isLockRequests());
    }

    @Test
    @DisplayName("toggleLockRequests unlock user requests when user requests is locked")
    void toggleLockRequests_UnlockUserRequests_WhenUserRequestsIsLocked() {
        BDDMockito
                .when(applicationUserServiceMock.toggleLockRequests(ArgumentMatchers.any(UUID.class)))
                .thenReturn(ApplicationUserCreator.createValidApplicationUser());

        ApplicationUser expectedApplicationUser = ApplicationUserCreator.createApplicationUserWithRequestsLocked();

        ResponseEntity<ApplicationUser> entity = applicationUserController.toggleLockRequests(expectedApplicationUser.getUuid());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedApplicationUser);

        assertThat(entity.getBody().isLockRequests()).isNotEqualTo(expectedApplicationUser.isLockRequests());
    }

    @Test
    @DisplayName("delete removes application user when successful")
    void delete_RemovesApplicationUser_WhenSuccessful() {
        Principal principalMock = Mockito.mock(Principal.class);

        ResponseEntity<Void> entity = applicationUserController.delete(UUID.randomUUID(), principalMock);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

}