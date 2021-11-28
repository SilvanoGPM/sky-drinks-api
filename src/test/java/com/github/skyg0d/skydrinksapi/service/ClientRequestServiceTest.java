package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.exception.BadRequestException;
import com.github.skyg0d.skydrinksapi.parameters.ClientRequestParameters;
import com.github.skyg0d.skydrinksapi.repository.request.ClientRequestRepository;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
                .when(clientRequestRepositoryMock.findAll(ArgumentMatchers.<Specification<ClientRequest>>any(), ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(drinkPage);

        BDDMockito
                .when(clientRequestRepositoryMock.save(ArgumentMatchers.any(ClientRequest.class)))
                .thenReturn(ClientRequestCreator.createValidClientRequest());

        BDDMockito
                .doNothing()
                .when(clientRequestRepositoryMock)
                .delete(ArgumentMatchers.any(ClientRequest.class));
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

        // TODO: Arrumar User
        Page<ClientRequest> drinkPage = clientRequestService.search(new ClientRequestParameters(), PageRequest.of(1, 1), null);

        assertThat(drinkPage).isNotNull();

        assertThat(drinkPage.toList())
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedClientRequest);
    }

    @Test
    @DisplayName("save creates client request when successful")
    void save_CreatesClientRequest_WhenSuccessful() {
        ClientRequest expectedClientRequest = ClientRequestCreator.createValidClientRequest();

        // TODO: Arrumar Principal.
        ClientRequest drinkSaved = clientRequestService.save(ClientRequestPostRequestBodyCreator.createClienteRequestPostRequestBodyToBeSave(), null);

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

        // TODO: Arrumar user.
        assertThatCode(() -> clientRequestService.replace(ClientRequestPutRequestBodyCreator.createClientRequestPutRequestBodyCreatorToBeUpdate(), null))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("finishRequest finish client request when successful")
    void finishRequest_FinishClientRequest_WhenSuccessful() {
        BDDMockito.when(clientRequestRepositoryMock.save(ArgumentMatchers.any(ClientRequest.class)))
                .thenReturn(ClientRequestCreator.createClientRequestFinished());

        ClientRequest expectedClientRequest = ClientRequestCreator.createClientRequestFinished();

        // TODO: Arrumar user.
        ClientRequest drinkFinished = clientRequestService.finishRequest(ClientRequestCreator.createValidClientRequest().getUuid(), null);

        assertThat(drinkFinished)
                .isNotNull()
                .isEqualTo(expectedClientRequest);

        assertThat(drinkFinished.isFinished()).isEqualTo(expectedClientRequest.isFinished());

        assertThat(drinkFinished.getTotalPrice()).isEqualTo(expectedClientRequest.getTotalPrice());
    }

    @Test
    @DisplayName("delete removes client request when successful")
    void delete_RemovesClientRequest_WhenSuccessful() {
        // TODO: Arrumar user.
        assertThatCode(() -> clientRequestService.delete(UUID.randomUUID(), null))
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
    @DisplayName("finishRequest creates client request when successful")
    void finishRequest_ThrowsBadRequestException_WhenClientRequestAlreadyFinished() {
        BDDMockito.when(clientRequestRepositoryMock.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(ClientRequestCreator.createClientRequestFinished()));

        assertThatExceptionOfType(BadRequestException.class)
                // TODO: Arrumar user.
                .isThrownBy(() -> clientRequestService.finishRequest(ClientRequestCreator.createValidClientRequest().getUuid(), null));
    }

}
