package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.domain.ClientRequestAlcoholicDrinkCount;
import com.github.skyg0d.skydrinksapi.domain.ClientRequestDrinkCount;
import com.github.skyg0d.skydrinksapi.parameters.ClientRequestParameters;
import com.github.skyg0d.skydrinksapi.property.WebSocketProperties;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPutRequestBody;
import com.github.skyg0d.skydrinksapi.service.ClientRequestService;
import com.github.skyg0d.skydrinksapi.socket.domain.ClientRequestStatusChanged;
import com.github.skyg0d.skydrinksapi.socket.domain.SocketMessage;
import com.github.skyg0d.skydrinksapi.util.AuthUtil;
import com.github.skyg0d.skydrinksapi.util.request.*;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for ClientRequestController")
class ClientRequestControllerTest {

    @InjectMocks
    private ClientRequestController clientRequestController;

    @Mock
    private ClientRequestService clientRequestServiceMock;

    @Mock
    private AuthUtil authUtilMock;

    @Mock
    private SimpMessagingTemplate templateMock;

    @Mock
    private WebSocketProperties webSocketPropertiesMock;

    @BeforeEach
    void setUp() {
        Page<ClientRequest> drinkPage = new PageImpl<>(List.of(ClientRequestCreator.createValidClientRequest()));

        BDDMockito
                .when(authUtilMock.getUser(ArgumentMatchers.any()))
                .thenReturn(ApplicationUserCreator.createValidApplicationUser());

        BDDMockito
                .when(clientRequestServiceMock.listAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(drinkPage);

        BDDMockito
                .when(clientRequestServiceMock.findByIdOrElseThrowBadRequestException(ArgumentMatchers.any(UUID.class)))
                .thenReturn(ClientRequestCreator.createValidClientRequest());

        BDDMockito
                .when(clientRequestServiceMock.search(ArgumentMatchers.any(ClientRequestParameters.class), ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(drinkPage);

        BDDMockito
                .when(clientRequestServiceMock.searchMyRequests(ArgumentMatchers.any(ClientRequestParameters.class), ArgumentMatchers.any(PageRequest.class), ArgumentMatchers.any(ApplicationUser.class)))
                .thenReturn(drinkPage);

        BDDMockito
                .when(clientRequestServiceMock.getTotalOfDrinksGroupedByAlcoholic(ArgumentMatchers.any(ApplicationUser.class)))
                .thenReturn(List.of(ClientRequestAlcoholicDrinkCountCreator.createClientRequestAlcoholicDrinkCount(), ClientRequestAlcoholicDrinkCountCreator.createClientRequestNotAlcoholicDrinkCount()));

        BDDMockito
                .when(clientRequestServiceMock.getMyTopFiveDrinks(ArgumentMatchers.any(ApplicationUser.class)))
                .thenReturn(List.of(ClientRequestDrinkCountCreator.createClientRequestDrinkCount()));

        BDDMockito
                .when(clientRequestServiceMock.save(ArgumentMatchers.any(ClientRequestPostRequestBody.class), ArgumentMatchers.any(ApplicationUser.class)))
                .thenReturn(ClientRequestCreator.createValidClientRequest());

        BDDMockito
                .doNothing()
                .when(clientRequestServiceMock)
                .replace(ArgumentMatchers.any(ClientRequestPutRequestBody.class), ArgumentMatchers.any(ApplicationUser.class));

        BDDMockito
                .when(clientRequestServiceMock.toggleBlockAllRequests())
                .thenReturn(true);

        BDDMockito
                .when(clientRequestServiceMock.finishRequest(ArgumentMatchers.any(UUID.class)))
                .thenReturn(ClientRequestCreator.createClientRequestFinished());

        BDDMockito
                .when(clientRequestServiceMock.cancelRequest(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(ApplicationUser.class)))
                .thenReturn(ClientRequestCreator.createClientRequestCanceled());

        BDDMockito
                .when(clientRequestServiceMock.deliverRequest(ArgumentMatchers.any(UUID.class)))
                .thenReturn(ClientRequestCreator.createClientRequestDelivered());


        BDDMockito
                .doNothing()
                .when(clientRequestServiceMock)
                .delete(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(ApplicationUser.class));

        BDDMockito
                .doNothing()
                .when(templateMock)
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(SocketMessage.class));

        BDDMockito
                .doNothing()
                .when(templateMock)
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(ClientRequestStatusChanged.class));


        BDDMockito
                .when(webSocketPropertiesMock.getSendClientRequestUpdateDelay())
                .thenReturn(10000L);

    }

    @Test
    @DisplayName("listAll return list of client requests inside page object when successful")
    void listAll_ReturnListOfClientRequestsInsidePageObject_WhenSuccessful() {
        ClientRequest expectedClientRequest = ClientRequestCreator.createValidClientRequest();

        ResponseEntity<Page<ClientRequest>> entity = clientRequestController.listAll(PageRequest.of(1, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedClientRequest);
    }

    @Test
    @DisplayName("listAll return empty page when there are no client requests")
    void listAll_ReturnListOfClientRequestsInsidePageObject_WhenThereAreNoClientRequests() {
        BDDMockito
                .when(clientRequestServiceMock.listAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(Page.empty());

        ResponseEntity<Page<ClientRequest>> entity = clientRequestController.listAll(PageRequest.of(1, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isEmpty();
    }

    @Test
    @DisplayName("findById returns an client request object when successful")
    void findById_ReturnsClientRequestObject_WhenSuccessful() {
        ClientRequest expectedClientRequest = ClientRequestCreator.createValidClientRequest();

        ResponseEntity<ClientRequest> entity = clientRequestController.findById(UUID.randomUUID());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedClientRequest);
    }

    @Test
    @DisplayName("search return list of client requests inside page object when successful")
    void search_ReturnListOfClientRequestsInsidePageObject_WhenSuccessful() {
        ClientRequest expectedClientRequest = ClientRequestCreator.createValidClientRequest();

        ResponseEntity<Page<ClientRequest>> entity = clientRequestController.search(new ClientRequestParameters(), PageRequest.of(1, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedClientRequest);
    }

    @Test
    @DisplayName("searchMyRequests return list of client requests inside page object when successful")
    void searchMyRequests_ReturnListOfClientRequestsInsidePageObject_WhenSuccessful() {
        Principal principalMock = Mockito.mock(Principal.class);

        ClientRequest expectedClientRequest = ClientRequestCreator.createValidClientRequest();

        ResponseEntity<Page<ClientRequest>> entity = clientRequestController.searchMyRequests(new ClientRequestParameters(), PageRequest.of(1, 1), principalMock);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedClientRequest);
    }

    @Test
    @DisplayName("countTotalDrinksInRequest returns client request drinks count when successful")
    void countTotalDrinksInRequest_ReturnsClientRequestDrinksCount_WhenSuccessful() {
        Principal principalMock = Mockito.mock(Principal.class);

        ClientRequestDrinkCount expectedDrinksCount = ClientRequestDrinkCountCreator.createClientRequestDrinkCount();

        ResponseEntity<List<ClientRequestDrinkCount>> entity = clientRequestController.getMyTopFiveDrinks(principalMock);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotEmpty()
                .hasSize(1);

        assertThat(entity.getBody().get(0)).isNotNull();

        assertThat(entity.getBody().get(0).getDrinkUUID())
                .isNotNull()
                .isEqualTo(expectedDrinksCount.getDrinkUUID());
    }

    @Test
    @DisplayName("countAlcoholicDrinksInRequests returns total of client requests grouped by alcoholic when successful")
    void countAlcoholicDrinksInRequests_ReturnsTotalOfClientRequestsGroupedByAlcoholic_WhenSuccessful() {
        Principal principalMock = Mockito.mock(Principal.class);

        ResponseEntity<List<ClientRequestAlcoholicDrinkCount>> entity = clientRequestController.getTotalOfDrinksGroupedByAlcoholic(principalMock);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotEmpty()
                .hasSize(2);

        assertThat(entity.getBody().get(0)).isNotNull();

        assertThat(entity.getBody().get(0).isAlcoholic()).isTrue();

        assertThat(entity.getBody().get(1)).isNotNull();

        assertThat(entity.getBody().get(1).isAlcoholic()).isFalse();
    }

    @Test
    @DisplayName("save creates client request when successful")
    void save_CreatesClientRequest_WhenSuccessful() {
        Principal principalMock = Mockito.mock(Principal.class);

        ClientRequest expectedClientRequest = ClientRequestCreator.createValidClientRequest();

        ResponseEntity<ClientRequest> entity = clientRequestController.save(ClientRequestPostRequestBodyCreator.createClientRequestPostRequestBodyToBeSave(), principalMock);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedClientRequest);
    }

    @Test
    @DisplayName("save returns 400 BadRequest when user not contains ROLE_USER")
    void save_Returns400BadRequest_WhenUserNotContainsROLE_USER() {
        ApplicationUser barmenUser = ApplicationUserCreator.createValidApplicationUser();

        barmenUser.setRole("BARMEN");

        BDDMockito
                .when(authUtilMock.getUser(ArgumentMatchers.any(Principal.class)))
                .thenReturn(barmenUser);

        Principal principalMock = Mockito.mock(Principal.class);

        ResponseEntity<ClientRequest> entity = clientRequestController.save(ClientRequestPostRequestBodyCreator.createClientRequestPostRequestBodyToBeSave(), principalMock);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("replace updates client request when successful")
    void replace_UpdatedClientRequest_WhenSuccessful() {
        Principal principalMock = Mockito.mock(Principal.class);

        ResponseEntity<Void> entity = clientRequestController.replace(ClientRequestPutRequestBodyCreator.createClientRequestPutRequestBodyCreatorToBeUpdate(), principalMock);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("toggleBlockAllRequests set blockAllRequests to true value when value is false")
    void toggleBlockAllRequests_SetBlockAllRequestsToTrue_WhenValueIsFalse() {
        ResponseEntity<Boolean> entity = clientRequestController.toggleBlockAllRequests();

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isTrue();
    }

    @Test
    @DisplayName("toggleBlockAllRequests set blockAllRequests to false value when value is true")
    void toggleBlockAllRequests_SetBlockAllRequestsToFalse_WhenValueIsTrue() {
        BDDMockito
                .when(clientRequestServiceMock.toggleBlockAllRequests())
                .thenReturn(false);

        ResponseEntity<Boolean> entity = clientRequestController.toggleBlockAllRequests();

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isFalse();
    }

    @Test
    @DisplayName("finishRequest finish client request when successful")
    void finishRequest_FinishClientRequest_WhenSuccessful() {
        ClientRequest expectedClientRequest = ClientRequestCreator.createClientRequestFinished();

        ResponseEntity<ClientRequest> entity = clientRequestController.finishRequest(ClientRequestCreator.createValidClientRequest().getUuid());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedClientRequest);

        assertThat(entity.getBody().getStatus()).isEqualTo(expectedClientRequest.getStatus());

        assertThat(entity.getBody().getTotalPrice()).isEqualTo(expectedClientRequest.getTotalPrice());
    }

    @Test
    @DisplayName("cancelRequest cancel client request when successful")
    void cancelRequest_CancelClientRequest_WhenSuccessful() {
        Principal principalMock = Mockito.mock(Principal.class);

        ClientRequest expectedClientRequest = ClientRequestCreator.createClientRequestCanceled();

        ResponseEntity<ClientRequest> entity = clientRequestController.cancelRequest(ClientRequestCreator.createValidClientRequest().getUuid(), principalMock);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedClientRequest);

        assertThat(entity.getBody().getStatus()).isEqualTo(expectedClientRequest.getStatus());

        assertThat(entity.getBody().getTotalPrice()).isEqualTo(expectedClientRequest.getTotalPrice());
    }

    @Test
    @DisplayName("deliverRequest deliver client request when successful")
    void deliverRequest_DeliverClientRequest_WhenSuccessful() {
        ClientRequest expectedClientRequest = ClientRequestCreator.createClientRequestDelivered();

        ResponseEntity<ClientRequest> entity = clientRequestController.deliverRequest(ClientRequestCreator.createClientRequestFinished().getUuid());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedClientRequest);

        assertThat(entity.getBody().isDelivered()).isEqualTo(expectedClientRequest.isDelivered());
    }

    @Test
    @DisplayName("delete removes client request when successful")
    void delete_RemovesClientRequest_WhenSuccessful() {
        Principal principalMock = Mockito.mock(Principal.class);

        ResponseEntity<Void> entity = clientRequestController.delete(UUID.randomUUID(), principalMock);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

}
