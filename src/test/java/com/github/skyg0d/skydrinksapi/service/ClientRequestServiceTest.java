package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.domain.*;
import com.github.skyg0d.skydrinksapi.exception.BadRequestException;
import com.github.skyg0d.skydrinksapi.exception.UserCannotCompleteClientRequestException;
import com.github.skyg0d.skydrinksapi.exception.UserCannotModifyClientRequestException;
import com.github.skyg0d.skydrinksapi.exception.UserRequestsAreLockedException;
import com.github.skyg0d.skydrinksapi.parameters.ClientRequestParameters;
import com.github.skyg0d.skydrinksapi.repository.request.ClientRequestRepository;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPutRequestBody;
import com.github.skyg0d.skydrinksapi.util.request.*;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserCreator;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for ClienteRequestService")
class ClientRequestServiceTest {

    @InjectMocks
    private ClientRequestService clientRequestService;

    @Mock
    private ClientRequestRepository clientRequestRepositoryMock;

    @Mock
    private DrinkService drinkServiceMock;

    @Mock
    private ApplicationUserService applicationUserServiceMock;


    @BeforeEach
    void setUp() {
        Page<ClientRequest> drinkPage = new PageImpl<>(List.of(ClientRequestCreator.createValidClientRequest()));

        BDDMockito
                .when(clientRequestRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(drinkPage);

        BDDMockito
                .when(clientRequestRepositoryMock.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(ClientRequestCreator.createValidClientRequest()));

        BDDMockito
                .when(clientRequestRepositoryMock.countAlcoholicDrinksInRequests(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(List.of(ClientRequestAlcoholicDrinkCountCreator.createClientRequestAlcoholicDrinkCount(), ClientRequestAlcoholicDrinkCountCreator.createClientRequestNotAlcoholicDrinkCount()));

        BDDMockito
                .when(clientRequestRepositoryMock.countTotalDrinksInRequest(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(List.of(ClientRequestDrinkCountCreator.createClientRequestDrinkCount()));

        BDDMockito
                .when(clientRequestRepositoryMock.countTotalDrinksInRequest(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(List.of(ClientRequestDrinkCountCreator.createClientRequestDrinkCount()));

        BDDMockito
                .when(clientRequestRepositoryMock.mostCanceledDrinks(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(List.of(ClientRequestDrinkCountCreator.createClientRequestDrinkCount()));

        BDDMockito
                .when(clientRequestRepositoryMock.getAllDatesInRequests())
                .thenReturn(List.of(ClientRequestDateCreator.createClientRequestDate()));

        BDDMockito
                .when(clientRequestRepositoryMock.findAll(ArgumentMatchers.<Specification<ClientRequest>>any(), ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(drinkPage);

        BDDMockito
                .when(clientRequestRepositoryMock.save(ArgumentMatchers.any(ClientRequest.class)))
                .thenReturn(ClientRequestCreator.createValidClientRequest());

        BDDMockito
                .doNothing()
                .when(clientRequestRepositoryMock)
                .delete(ArgumentMatchers.any(ClientRequest.class));

        BDDMockito
                .when(applicationUserServiceMock.findByIdOrElseThrowBadRequestException(ArgumentMatchers.any(UUID.class)))
                .thenReturn(ApplicationUserCreator.createValidApplicationUser());
    }

    @Test
    @DisplayName("listAll return list of client requests inside page object when successful")
    void listAll_ReturnListOfClientRequestsInsidePageObject_WhenSuccessful() {
        ClientRequest expectedClientRequest = ClientRequestCreator.createValidClientRequest();

        Page<ClientRequest> drinkPage = clientRequestService.listAll(PageRequest.of(1, 1));

        assertThat(drinkPage).isNotNull();

        assertThat(drinkPage.toList())
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedClientRequest);
    }

    @Test
    @DisplayName("listAll return empty page when there are no client requests")
    void listAll_ReturnListOfClientRequestsInsidePageObject_WhenThereAreNoClientRequests() {
        BDDMockito
                .when(clientRequestRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(Page.empty());

        Page<ClientRequest> drinkPage = clientRequestService.listAll(PageRequest.of(1, 1));

        assertThat(drinkPage).isEmpty();
    }

    @Test
    @DisplayName("findByIdOrElseThrowBadRequestException returns an client request object when successful")
    void findByIdOrElseThrowBadRequestException_ReturnsClientRequestObject_WhenSuccessful() {
        ClientRequest expectedClientRequest = ClientRequestCreator.createValidClientRequest();

        ClientRequest drinkFound = clientRequestService.findByIdOrElseThrowBadRequestException(UUID.randomUUID());

        assertThat(drinkFound)
                .isNotNull()
                .isEqualTo(expectedClientRequest);
    }

    @Test
    @DisplayName("search return list of client requests inside page object when successful")
    void search_ReturnListOfClientRequestsInsidePageObject_WhenSuccessful() {
        ClientRequest expectedClientRequest = ClientRequestCreator.createValidClientRequest();

        expectedClientRequest.getUser().setRole("BARMEN");

        Page<ClientRequest> drinkPage = clientRequestService.search(new ClientRequestParameters(), PageRequest.of(1, 1));

        assertThat(drinkPage).isNotNull();

        assertThat(drinkPage.toList())
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedClientRequest);
    }

    @Test
    @DisplayName("searchMyRequests return list of client requests inside page object when successful")
    void searchMyRequests_ReturnListOfClientRequestsInsidePageObject_WhenSuccessful() {
        ClientRequest expectedClientRequest = ClientRequestCreator.createValidClientRequest();

        Page<ClientRequest> drinkPage = clientRequestService.searchMyRequests(new ClientRequestParameters(), PageRequest.of(1, 1), expectedClientRequest.getUser());

        assertThat(drinkPage).isNotNull();

        assertThat(drinkPage.toList())
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedClientRequest);
    }

    @Test
    @DisplayName("getMyTopFiveDrinks returns client request drinks count when successful")
    void getMyTopFiveDrinks_ReturnsClientRequestDrinksCount_WhenSuccessful() {
        ClientRequestDrinkCount expectedDrinksCount = ClientRequestDrinkCountCreator.createClientRequestDrinkCount();

        List<ClientRequestDrinkCount> myTopFiveDrinks = clientRequestService.getMyTopFiveDrinks(ApplicationUserCreator.createValidApplicationUser());

        assertThat(myTopFiveDrinks)
                .isNotEmpty()
                .hasSize(1);

        assertThat(myTopFiveDrinks.get(0)).isNotNull();

        assertThat(myTopFiveDrinks.get(0).getDrinkUUID())
                .isNotNull()
                .isEqualTo(expectedDrinksCount.getDrinkUUID());
    }

    @Test
    @DisplayName("getTopFiveDrinks returns client request drinks count when successful")
    void getTopFiveDrinks_ReturnsClientRequestDrinksCount_WhenSuccessful() {
        ClientRequestDrinkCount expectedDrinksCount = ClientRequestDrinkCountCreator.createClientRequestDrinkCount();

        List<ClientRequestDrinkCount> myTopFiveDrinks = clientRequestService.getTopFiveDrinks(UUID.randomUUID());

        assertThat(myTopFiveDrinks)
                .isNotEmpty()
                .hasSize(1);

        assertThat(myTopFiveDrinks.get(0)).isNotNull();

        assertThat(myTopFiveDrinks.get(0).getDrinkUUID())
                .isNotNull()
                .isEqualTo(expectedDrinksCount.getDrinkUUID());
    }

    @Test
    @DisplayName("getTopDrinksInRequests returns client request drinks count of all users when successful")
    void getTopDrinksInRequests_ReturnsClientRequestDrinksCountOfAllUsers_WhenSuccessful() {
        ClientRequestDrinkCount expectedDrinksCount = ClientRequestDrinkCountCreator.createClientRequestDrinkCount();

        List<ClientRequestDrinkCount> topDrinks = clientRequestService.getTopDrinksInRequests(PageRequest.of(0, 1));

        assertThat(topDrinks)
                .isNotEmpty()
                .hasSize(1);

        assertThat(topDrinks.get(0)).isNotNull();

        assertThat(topDrinks.get(0).getDrinkUUID())
                .isNotNull()
                .isEqualTo(expectedDrinksCount.getDrinkUUID());
    }

    @Test
    @DisplayName("mostCanceledDrinks returns client request drinks count of most canceled requests when successful")
    void mostCanceledDrinks_ReturnsClientRequestDrinksCountOfMostCanceledRequests_WhenSuccessful() {
        ClientRequestDrinkCount expectedDrinksCount = ClientRequestDrinkCountCreator.createClientRequestDrinkCount();

        List<ClientRequestDrinkCount> mostCanceledDrinks = clientRequestService.mostCanceledDrinks(PageRequest.of(0, 1));

        assertThat(mostCanceledDrinks)
                .isNotEmpty()
                .hasSize(1);

        assertThat(mostCanceledDrinks.get(0)).isNotNull();

        assertThat(mostCanceledDrinks.get(0).getDrinkUUID())
                .isNotNull()
                .isEqualTo(expectedDrinksCount.getDrinkUUID());
    }

    @Test
    @DisplayName("getTotalOfDrinksGroupedByAlcoholic returns total of client requests grouped by alcoholic when successful")
    void getTotalOfDrinksGroupedByAlcoholic_ReturnsTotalOfClientRequestsGroupedByAlcoholic_WhenSuccessful() {
        List<ClientRequestAlcoholicDrinkCount> allDrinksOfRequests = clientRequestService.getTotalOfDrinksGroupedByAlcoholic(ApplicationUserCreator.createValidApplicationUser());

        assertThat(allDrinksOfRequests)
                .isNotEmpty()
                .hasSize(2);

        assertThat(allDrinksOfRequests.get(0)).isNotNull();

        assertThat(allDrinksOfRequests.get(0).isAlcoholic()).isTrue();

        assertThat(allDrinksOfRequests.get(1)).isNotNull();

        assertThat(allDrinksOfRequests.get(1).isAlcoholic()).isFalse();
    }

    @Test
    @DisplayName("getAllDatesInRequests returns all dates in requests when successful")
    void getAllDatesInRequests_ReturnsAllDatesInRequests_WhenSuccessful() {
        List<ClientRequestDate> datesFound = clientRequestService.getAllDatesInRequests();

        assertThat(datesFound)
                .isNotEmpty()
                .hasSize(1);

        assertThat(datesFound.get(0)).isNotNull();

        assertThat(datesFound.get(0).getDate())
                .isNotNull()
                .isEqualTo(ClientRequestDateCreator.createClientRequestDate().getDate());
    }

    @Test
    @DisplayName("getAllBlocked returns boolean of all users is blocked when successful")
    void getAllBlocked_ReturnsBooleanOfAllUsersIsBlocked_WhenSuccessful() {
        boolean allBlocked = clientRequestService.getAllBlocked();
        assertThat(allBlocked).isFalse();
    }

    @Test
    @DisplayName("save creates client request when successful")
    void save_CreatesClientRequest_WhenSuccessful() {
        ClientRequest expectedClientRequest = ClientRequestCreator.createValidClientRequest();

        BDDMockito
                .when(drinkServiceMock.findByIdOrElseThrowBadRequestException(ArgumentMatchers.any(UUID.class)))
                .thenReturn(expectedClientRequest.getDrinks().get(0));

        ClientRequest drinkSaved = clientRequestService.save(ClientRequestPostRequestBodyCreator.createClientRequestPostRequestBodyToBeSave(), expectedClientRequest.getUser());

        assertThat(drinkSaved)
                .isNotNull()
                .isEqualTo(expectedClientRequest);
    }

    @Test
    @DisplayName("replace updates client request when successful")
    void replace_UpdatedClientRequest_WhenSuccessful() {
        BDDMockito
                .when(clientRequestRepositoryMock.save(ArgumentMatchers.any(ClientRequest.class)))
                .thenReturn(ClientRequestCreator.createValidUpdatedClientRequest());

        ClientRequestPutRequestBody requestToUpdate = ClientRequestPutRequestBodyCreator.createClientRequestPutRequestBodyCreatorToBeUpdate();

        assertThatCode(() -> clientRequestService.replace(requestToUpdate, ClientRequestCreator.createValidUpdatedClientRequest().getUser()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("toggleBlockAllRequests set blockAllRequests to true value when value is false")
    void toggleBlockAllRequests_SetBlockAllRequestsToTrue_WhenValueIsFalse() {
        boolean allBlocked = clientRequestService.toggleBlockAllRequests();
        assertThat(allBlocked).isTrue();
    }

    @Test
    @DisplayName("toggleBlockAllRequests set blockAllRequests to false value when value is true")
    void toggleBlockAllRequests_SetBlockAllRequestsToFalse_WhenValueIsTrue() {
        boolean allBlocked1 = clientRequestService.toggleBlockAllRequests();
        boolean allBlocked2 = clientRequestService.toggleBlockAllRequests();

        assertThat(allBlocked1).isTrue();
        assertThat(allBlocked2).isFalse();
    }

    @Test
    @DisplayName("finishRequest finish client request when successful")
    void finishRequest_FinishClientRequest_WhenSuccessful() {
        BDDMockito.when(clientRequestRepositoryMock.save(ArgumentMatchers.any(ClientRequest.class)))
                .thenReturn(ClientRequestCreator.createClientRequestFinished());

        ClientRequest expectedClientRequest = ClientRequestCreator.createClientRequestFinished();

        ClientRequest requestValid = ClientRequestCreator.createValidClientRequest();

        ClientRequest requestFinished = clientRequestService.finishRequest(requestValid.getUuid());

        assertThat(requestFinished)
                .isNotNull()
                .isEqualTo(expectedClientRequest);

        assertThat(requestFinished.getStatus()).isEqualTo(expectedClientRequest.getStatus());

        assertThat(requestFinished.getTotalPrice()).isEqualTo(expectedClientRequest.getTotalPrice());
    }

    @Test
    @DisplayName("cancelRequest cancel client request when successful")
    void cancelRequest_CancelClientRequest_WhenSuccessful() {
        BDDMockito.when(clientRequestRepositoryMock.save(ArgumentMatchers.any(ClientRequest.class)))
                .thenReturn(ClientRequestCreator.createClientRequestCanceled());

        ClientRequest expectedClientRequest = ClientRequestCreator.createClientRequestCanceled();

        ClientRequest requestValid = ClientRequestCreator.createValidClientRequest();

        ClientRequest requestCanceled = clientRequestService.cancelRequest(requestValid.getUuid(), requestValid.getUser());

        assertThat(requestCanceled)
                .isNotNull()
                .isEqualTo(expectedClientRequest);

        assertThat(requestCanceled.getStatus()).isEqualTo(expectedClientRequest.getStatus());

        assertThat(requestCanceled.getTotalPrice()).isEqualTo(expectedClientRequest.getTotalPrice());
    }

    @Test
    @DisplayName("deliverRequest deliver client request when successful")
    void deliverRequest_DeliverClientRequest_WhenSuccessful() {
        BDDMockito.when(clientRequestRepositoryMock.save(ArgumentMatchers.any(ClientRequest.class)))
                .thenReturn(ClientRequestCreator.createClientRequestDelivered());

        BDDMockito
                .when(clientRequestRepositoryMock.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(ClientRequestCreator.createClientRequestFinished()));

        ClientRequest expectedClientRequest = ClientRequestCreator.createClientRequestDelivered();

        ClientRequest requestValid = ClientRequestCreator.createClientRequestFinished();

        ClientRequest requestDelivered = clientRequestService.deliverRequest(requestValid.getUuid());

        assertThat(requestDelivered)
                .isNotNull()
                .isEqualTo(expectedClientRequest);

        assertThat(requestDelivered.isDelivered()).isEqualTo(expectedClientRequest.isDelivered());
    }

    @Test
    @DisplayName("delete removes client request when successful")
    void delete_RemovesClientRequest_WhenSuccessful() {
        assertThatCode(() -> clientRequestService.delete(UUID.randomUUID(), ApplicationUserCreator.createValidApplicationUser()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("findByIdOrElseThrowBadRequestException throws BadRequestException when client request is not found")
    void findByIdOrElseThrowBadRequestException_ThrowsBadRequestException_WhenClientRequestIsNotFound() {
        BDDMockito
                .when(clientRequestRepositoryMock.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> clientRequestService.findByIdOrElseThrowBadRequestException(UUID.randomUUID()));
    }

    @Test
    @DisplayName("save throws UserCannotCompleteClientRequestException when drinks contains alcoholic and user is minor")
    void save_ThrowsUserCannotCompleteClientRequestException_WhenDrinksContainsAlcoholicAndUserIsMinor() {
        ClientRequest expectedClientRequest = ClientRequestCreator.createValidClientRequest();

        ApplicationUser user = expectedClientRequest.getUser();

        user.setBirthDay(LocalDate.now());

        Drink alcoholicDrink = expectedClientRequest.getDrinks().get(0);

        alcoholicDrink.setAlcoholic(true);

        BDDMockito
                .when(drinkServiceMock.findByIdOrElseThrowBadRequestException(ArgumentMatchers.any(UUID.class)))
                .thenReturn(alcoholicDrink);

        assertThatExceptionOfType(UserCannotCompleteClientRequestException.class)
                .isThrownBy(() -> clientRequestService.save(ClientRequestPostRequestBodyCreator.createClientRequestPostRequestBodyToBeSave(), user));
    }

    @Test
    @DisplayName("save throws UserRequestsAreLockedException when requests is locked")
    void save_ThrowsUserRequestsAreLockedException_WhenRequestsIsLocked() {
        ClientRequest expectedClientRequest = ClientRequestCreator.createValidClientRequest();

        ApplicationUser user = expectedClientRequest.getUser();

        user.setLockRequests(true);

        user.setLockRequestsTimestamp(LocalDateTime.now());

        assertThatExceptionOfType(UserRequestsAreLockedException.class)
                .isThrownBy(() -> clientRequestService.save(ClientRequestPostRequestBodyCreator.createClientRequestPostRequestBodyToBeSave(), user));
    }

    @Test
    @DisplayName("save throws BadRequestException when all users is blocked")
    void save_ThrowsBadRequestException_WhenAllUsersIsBlocked() {
        ClientRequest expectedClientRequest = ClientRequestCreator.createValidClientRequest();

        ApplicationUser user = expectedClientRequest.getUser();

        clientRequestService.toggleBlockAllRequests();

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> clientRequestService.save(ClientRequestPostRequestBodyCreator.createClientRequestPostRequestBodyToBeSave(), user));
    }

    @Test
    @DisplayName("finishRequest throws BadRequestException when client request already finished")
    void finishRequest_ThrowsBadRequestException_WhenClientRequestAlreadyFinished() {
        BDDMockito.when(clientRequestRepositoryMock.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(ClientRequestCreator.createClientRequestFinished()));

        ClientRequest requestValid = ClientRequestCreator.createClientRequestFinished();

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> clientRequestService.finishRequest(requestValid.getUuid()));
    }

    @Test
    @DisplayName("finishRequest throws BadRequestException when client request is canceled")
    void finishRequest_ThrowsBadRequestException_WhenClientRequestIsCanceled() {
        BDDMockito.when(clientRequestRepositoryMock.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(ClientRequestCreator.createClientRequestCanceled()));

        ClientRequest requestValid = ClientRequestCreator.createClientRequestCanceled();

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> clientRequestService.finishRequest(requestValid.getUuid()));
    }

    @Test
    @DisplayName("cancelRequest throws BadRequestException when client request already canceled")
    void cancelRequest_ThrowsBadRequestException_WhenClientRequestAlreadyCanceled() {
        BDDMockito.when(clientRequestRepositoryMock.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(ClientRequestCreator.createClientRequestCanceled()));

        ClientRequest requestValid = ClientRequestCreator.createClientRequestCanceled();

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> clientRequestService.cancelRequest(requestValid.getUuid(), requestValid.getUser()));
    }

    @Test
    @DisplayName("cancelRequest throws BadRequestException when client request is finished and delivered")
    void cancelRequest_ThrowsBadRequestException_WhenClientRequestIsFinishedAndDelivered() {
        BDDMockito.when(clientRequestRepositoryMock.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(ClientRequestCreator.createClientRequestDelivered()));

        ClientRequest requestValid = ClientRequestCreator.createClientRequestDelivered();

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> clientRequestService.cancelRequest(requestValid.getUuid(), requestValid.getUser()));
    }

    @Test
    @DisplayName("cancelRequest throws UserCannotModifyClientRequestException when user do not have access")
    void finishRequest_ThrowsUserCannotModifyClientRequestException_WhenUserDoNotHaveAccess() {
        BDDMockito.when(clientRequestRepositoryMock.save(ArgumentMatchers.any(ClientRequest.class)))
                .thenReturn(ClientRequestCreator.createClientRequestFinished());

        ClientRequest requestValid = ClientRequestCreator.createValidClientRequest();

        ApplicationUser someUser = requestValid.getUser();

        someUser.setUuid(UUID.randomUUID());

        assertThatExceptionOfType(UserCannotModifyClientRequestException.class)
                .isThrownBy(() -> clientRequestService.cancelRequest(requestValid.getUuid(), someUser));
    }

    @Test
    @DisplayName("deliverRequest throws BadRequestException when client request is finished")
    void deliverRequest_ThrowsBadRequestException_WhenClientRequestIsNotFinished() {
        BDDMockito.when(clientRequestRepositoryMock.save(ArgumentMatchers.any(ClientRequest.class)))
                .thenReturn(ClientRequestCreator.createValidClientRequest());

        ClientRequest requestValid = ClientRequestCreator.createValidClientRequest();

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> clientRequestService.deliverRequest(requestValid.getUuid()));
    }

    @Test
    @DisplayName("deliverRequest throws BadRequestException when client request already delivered")
    void deliverRequest_ThrowsBadRequestException_WhenClientRequestAlreadyDelivered() {
        BDDMockito.when(clientRequestRepositoryMock.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(ClientRequestCreator.createClientRequestDelivered()));

        ClientRequest requestValid = ClientRequestCreator.createClientRequestDelivered();

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> clientRequestService.deliverRequest(requestValid.getUuid()));
    }


}
