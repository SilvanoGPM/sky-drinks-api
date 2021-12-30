package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.exception.ActionNotAllowedException;
import com.github.skyg0d.skydrinksapi.exception.BadRequestException;
import com.github.skyg0d.skydrinksapi.exception.UserUniqueFieldExistsException;
import com.github.skyg0d.skydrinksapi.parameters.ApplicationUserParameters;
import com.github.skyg0d.skydrinksapi.repository.request.ClientRequestRepository;
import com.github.skyg0d.skydrinksapi.repository.user.ApplicationUserRepository;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserCreator;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserPostRequestBodyCreator;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserPutRequestBodyCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for ApplicationUserService")
class ApplicationUserServiceTest {

    @InjectMocks
    private ApplicationUserService applicationUserService;

    @Mock
    private ApplicationUserRepository applicationUserRepositoryMock;

    @Mock
    private ClientRequestRepository clientRequestRepositoryMock;

    @BeforeEach
    void setUp() {
        PageImpl<ApplicationUser> applicationUsersPage = new PageImpl<>(List.of(ApplicationUserCreator.createValidApplicationUser()));

        BDDMockito
                .when(applicationUserRepositoryMock.findAll(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(applicationUsersPage);

        BDDMockito
                .when(applicationUserRepositoryMock.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(ApplicationUserCreator.createValidApplicationUser()));

        BDDMockito
                .when(applicationUserRepositoryMock.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(ApplicationUserCreator.createValidApplicationUser()));

        BDDMockito
                .when(applicationUserRepositoryMock.findByCpf(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(ApplicationUserCreator.createValidApplicationUser()));

        BDDMockito
                .when(applicationUserRepositoryMock.findAll(ArgumentMatchers.<Specification<ApplicationUser>>any(), ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(applicationUsersPage);

        BDDMockito
                .when(applicationUserRepositoryMock.save(ArgumentMatchers.any(ApplicationUser.class)))
                .thenReturn(ApplicationUserCreator.createValidApplicationUser());

        BDDMockito
                .doNothing()
                .when(applicationUserRepositoryMock)
                .delete(ArgumentMatchers.any(ApplicationUser.class));
    }

    @Test
    @DisplayName("listAll return list of application users inside page object when successful")
    void listAll_ReturnListOfApplicationUsersInsidePageObject_WhenSuccessful() {
        ApplicationUser expectedApplicationUser = ApplicationUserCreator.createValidApplicationUser();

        Page<ApplicationUser> applicationUsersPage = applicationUserService.listAll(PageRequest.of(1, 1));

        assertThat(applicationUsersPage)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedApplicationUser);
    }

    @Test
    @DisplayName("listAll return empty page when there are no application users")
    void listAll_ReturnListOfDrinksInsidePageObject_WhenThereAreNoApplicationUsers() {
        BDDMockito
                .when(applicationUserRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(Page.empty());

        Page<ApplicationUser> applicationUsersPage = applicationUserService.listAll(PageRequest.of(1, 1));

        assertThat(applicationUsersPage).isEmpty();
    }

    @Test
    @DisplayName("search return list of application users inside page object when successful")
    void search_ReturnListOfApplicationUsersInsidePageObject_WhenSuccessful() {
        ApplicationUser expectedApplicationUser = ApplicationUserCreator.createValidApplicationUser();

        Page<ApplicationUser> applicationUsersPage = applicationUserService.search(new ApplicationUserParameters(), PageRequest.of(1, 1));

        assertThat(applicationUsersPage)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedApplicationUser);
    }

    @Test
    @DisplayName("findByIdEmail return application user when successful")
    void findByEmail_ReturnApplicationUser_WhenSuccessful() {
        ApplicationUser expectedApplicationUser = ApplicationUserCreator.createValidApplicationUser();

        ApplicationUser applicationUserFound = applicationUserService.findByEmail(expectedApplicationUser.getEmail());

        assertThat(applicationUserFound)
                .isNotNull()
                .isEqualTo(expectedApplicationUser);
    }

    @Test
    @DisplayName("findByCpf return application user when successful")
    void findByCpf_ReturnApplicationUser_WhenSuccessful() {
        ApplicationUser expectedApplicationUser = ApplicationUserCreator.createValidApplicationUser();

        ApplicationUser applicationUserFound = applicationUserService.findByCpf(expectedApplicationUser.getCpf());

        assertThat(applicationUserFound)
                .isNotNull()
                .isEqualTo(expectedApplicationUser);
    }

    @Test
    @DisplayName("findByIdOrElseThrowBadRequestException return application user when successful")
    void findByIdOrElseThrowBadRequestException_ReturnApplicationUser_WhenSuccessful() {
        ApplicationUser expectedApplicationUser = ApplicationUserCreator.createValidApplicationUser();

        ApplicationUser applicationUserFound = applicationUserService.findByIdOrElseThrowBadRequestException(UUID.randomUUID());

        assertThat(applicationUserFound)
                .isNotNull()
                .isEqualTo(expectedApplicationUser);
    }

    @Test
    @DisplayName("getStaffUsers return list of application users when successful")
    void findByCpf_ReturnListOfApplicationUser_WhenSuccessful() {
        ApplicationUser adminUser = ApplicationUserCreator.createAdminApplicationUser();
        ApplicationUser barmenUser = ApplicationUserCreator.createBarmenApplicationUser();
        ApplicationUser waiterUser = ApplicationUserCreator.createWaiterApplicationUser();
        ApplicationUser user = ApplicationUserCreator.createValidApplicationUser();

        BDDMockito
                .when(applicationUserRepositoryMock.findAll(ArgumentMatchers.<Specification<ApplicationUser>>any()))
                .thenReturn(List.of(adminUser, barmenUser, waiterUser));

        List<ApplicationUser> applicationUserFound = applicationUserService.getStaffUsers();

        assertThat(applicationUserFound)
                .isNotNull()
                .contains(adminUser, barmenUser, waiterUser)
                .doesNotContain(user);
    }

    @Test
    @DisplayName("save creates application user when successful")
    void save_CreatesApplicationUser_WhenSuccessful() {
        BDDMockito
                .when(applicationUserRepositoryMock.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        BDDMockito
                .when(applicationUserRepositoryMock.findByCpf(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        ApplicationUser expectedApplicationUser = ApplicationUserCreator.createValidApplicationUser();

        ApplicationUser applicationUserSaved = applicationUserService.save(ApplicationUserPostRequestBodyCreator.createApplicationUserPostRequestBodyToBeSave());

        assertThat(applicationUserSaved)
                .isNotNull()
                .isEqualTo(expectedApplicationUser);
    }

    @Test
    @DisplayName("replace updates application user when successful")
    void replace_UpdatedApplicationUser_WhenSuccessful() {
        ApplicationUser user = ApplicationUserCreator.createValidApplicationUser();

        BDDMockito
                .when(applicationUserRepositoryMock.save(ArgumentMatchers.any(ApplicationUser.class)))
                .thenReturn(ApplicationUserCreator.createUpdatedApplicationUser());


        assertThatCode(() -> applicationUserService.replace(ApplicationUserPutRequestBodyCreator.createApplicationUserPutRequestBodyToBeSave(), user))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("toggleLockRequests lock user requests when user requests is unlocked")
    void toggleLockRequests_LockUserRequests_WhenUserRequestsIsUnlocked() {
        BDDMockito
                .when(applicationUserRepositoryMock.save(ArgumentMatchers.any(ApplicationUser.class)))
                .thenReturn(ApplicationUserCreator.createApplicationUserWithRequestsLocked());

        ApplicationUser expectedApplicationUser = ApplicationUserCreator.createValidApplicationUser();

        ApplicationUser applicationUserSaved = applicationUserService.toggleLockRequests(expectedApplicationUser.getUuid());

        assertThat(applicationUserSaved)
                .isNotNull()
                .isEqualTo(expectedApplicationUser);

        assertThat(applicationUserSaved.isLockRequests()).isNotEqualTo(expectedApplicationUser.isLockRequests());
    }

    @Test
    @DisplayName("toggleLockRequests unlock user requests when user requests is locked")
    void toggleLockRequests_UnlockUserRequests_WhenUserRequestsIsLocked() {
        ApplicationUser expectedApplicationUser = ApplicationUserCreator.createApplicationUserWithRequestsLocked();

        ApplicationUser applicationUserSaved = applicationUserService.toggleLockRequests(expectedApplicationUser.getUuid());

        assertThat(applicationUserSaved)
                .isNotNull()
                .isEqualTo(expectedApplicationUser);

        assertThat(applicationUserSaved.isLockRequests()).isNotEqualTo(expectedApplicationUser.isLockRequests());
    }

    @Test
    @DisplayName("delete removes drink when successful")
    void delete_RemovesDrink_WhenSuccessful() {
        ApplicationUser user = ApplicationUserCreator.createValidApplicationUser();

        assertThatCode(() -> applicationUserService.delete(user.getUuid(), user))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("findByIdOrElseThrowBadRequestException throws BadRequestException when application user is not found")
    void findByIdOrElseThrowBadRequestException_ThrowsBadRequestException_WhenApplicationUserIsNotFound() {
        BDDMockito
                .when(applicationUserRepositoryMock.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> applicationUserService.findByIdOrElseThrowBadRequestException(UUID.randomUUID()));
    }

    @Test
    @DisplayName("findByEmail throws BadRequestException when application user is not found")
    void findByEmail_ThrowsBadRequestException_WhenApplicationUserIsNotFound() {
        BDDMockito
                .when(applicationUserRepositoryMock.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> applicationUserService.findByEmail(""));
    }

    @Test
    @DisplayName("findByCpf throws BadRequestException when application user is not found")
    void findByCpf_ThrowsBadRequestException_WhenApplicationUserIsNotFound() {
        BDDMockito
                .when(applicationUserRepositoryMock.findByCpf(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> applicationUserService.findByCpf(""));
    }

    @Test
    @DisplayName("save throws UserUniqueFieldExistsException when email already exists")
    void save_ThrowsUserUniqueFieldExistsException_WhenEmailAlreadyExists() {
        BDDMockito
                .when(applicationUserRepositoryMock.findByCpf(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(UserUniqueFieldExistsException.class)
                .isThrownBy(() -> applicationUserService.save(ApplicationUserPostRequestBodyCreator.createApplicationUserPostRequestBodyToBeSave()));
    }

    @Test
    @DisplayName("save throws UserUniqueFieldExistsException when cpf already exists")
    void save_ThrowsUserUniqueFieldExistsException_WhenCpfAlreadyExists() {
        BDDMockito
                .when(applicationUserRepositoryMock.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(UserUniqueFieldExistsException.class)
                .isThrownBy(() -> applicationUserService.save(ApplicationUserPostRequestBodyCreator.createApplicationUserPostRequestBodyToBeSave()));
    }

    @Test
    @DisplayName("replace throws ActionNotAllowedException when user does not have permission to modify user")
    void replace_ThrowsActionNotAllowedException_WhenUserDoesNotHavePermissionToModifyUser() {
        ApplicationUser user = ApplicationUserCreator.createValidApplicationUser();

        user.setUuid(UUID.randomUUID());

        assertThatExceptionOfType(ActionNotAllowedException.class)
                .isThrownBy(() -> applicationUserService.replace(ApplicationUserPutRequestBodyCreator.createApplicationUserPutRequestBodyToBeSave(), user));
    }

    @Test
    @DisplayName("delete throws ActionNotAllowedException when user does not have permission to modify user")
    void delete_ThrowsActionNotAllowedException_WhenUserDoesNotHavePermissionToModifyUser() {
        ApplicationUser user = ApplicationUserCreator.createValidApplicationUser();

        assertThatExceptionOfType(ActionNotAllowedException.class)
                .isThrownBy(() -> applicationUserService.delete(UUID.randomUUID(), user));
    }

}