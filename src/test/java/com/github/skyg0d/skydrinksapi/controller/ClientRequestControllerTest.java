package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.parameters.ClientRequestParameters;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPutRequestBody;
import com.github.skyg0d.skydrinksapi.service.ClientRequestService;
import com.github.skyg0d.skydrinksapi.util.request.ClientRequestCreator;
import com.github.skyg0d.skydrinksapi.util.request.ClientRequestPostRequestBodyCreator;
import com.github.skyg0d.skydrinksapi.util.request.ClientRequestPutRequestBodyCreator;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for ClientRequestController")
class ClientRequestControllerTest {

    @InjectMocks
    private ClientRequestController clientRequestController;

    @Mock
    private ClientRequestService clientRequestServiceMock;

    @BeforeEach
    void setUp() {
        Page<ClientRequest> drinkPage = new PageImpl<>(List.of(ClientRequestCreator.createValidClientRequest()));

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
                .when(clientRequestServiceMock.save(ArgumentMatchers.any(ClientRequestPostRequestBody.class)))
                .thenReturn(ClientRequestCreator.createValidClientRequest());

        BDDMockito
                .doNothing()
                .when(clientRequestServiceMock)
                .replace(ArgumentMatchers.any(ClientRequestPutRequestBody.class));

        BDDMockito
                .when(clientRequestServiceMock.finishRequest(ArgumentMatchers.any(UUID.class)))
                .thenReturn(ClientRequestCreator.createClientRequestFinished());

        BDDMockito
                .doNothing()
                .when(clientRequestServiceMock)
                .delete(ArgumentMatchers.any(UUID.class));
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
    @DisplayName("save creates client request when successful")
    void save_CreatesClientRequest_WhenSuccessful() {
        ClientRequest expectedClientRequest = ClientRequestCreator.createValidClientRequest();

        ResponseEntity<ClientRequest> entity = clientRequestController.save(ClientRequestPostRequestBodyCreator.createClienteRequestPostRequestBodyToBeSave());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedClientRequest);
    }

    @Test
    @DisplayName("replace updates client request when successful")
    void replace_UpdatedClientRequest_WhenSuccessful() {
        ResponseEntity<Void> entity = clientRequestController.replace(ClientRequestPutRequestBodyCreator.createClientRequestPutRequestBodyCreatorToBeUpdate());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
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

        assertThat(entity.getBody().isFinished()).isEqualTo(expectedClientRequest.isFinished());

        assertThat(entity.getBody().getTotalPrice()).isEqualTo(expectedClientRequest.getTotalPrice());
    }

    @Test
    @DisplayName("delete removes client request when successful")
    void delete_RemovesClientRequest_WhenSuccessful() {
        ResponseEntity<Void> entity = clientRequestController.delete(UUID.randomUUID());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

}
