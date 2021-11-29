package com.github.skyg0d.skydrinksapi.integration;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.domain.Table;
import com.github.skyg0d.skydrinksapi.repository.drink.DrinkRepository;
import com.github.skyg0d.skydrinksapi.repository.request.ClientRequestRepository;
import com.github.skyg0d.skydrinksapi.repository.table.TableRepository;
import com.github.skyg0d.skydrinksapi.repository.user.ApplicationUserRepository;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPutRequestBody;
import com.github.skyg0d.skydrinksapi.util.TokenUtil;
import com.github.skyg0d.skydrinksapi.util.drink.DrinkCreator;
import com.github.skyg0d.skydrinksapi.util.request.ClientRequestCreator;
import com.github.skyg0d.skydrinksapi.util.request.ClientRequestPostRequestBodyCreator;
import com.github.skyg0d.skydrinksapi.util.request.ClientRequestPutRequestBodyCreator;
import com.github.skyg0d.skydrinksapi.util.table.TableCreator;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserCreator;
import com.github.skyg0d.skydrinksapi.wrapper.PageableResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Integration Tests for ClientRequestController")
class ClientRequestControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ClientRequestRepository clientRequestRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private DrinkRepository drinkRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private TokenUtil tokenUtil;

    @Test
    @DisplayName("listAll return list of client requests inside page object when successful")
    void listAll_ReturnListOfClientRequestsInsidePageObject_WhenSuccessful() {
        ClientRequest clientRequestSaved = persistClientRequest();

        ResponseEntity<PageableResponse<ClientRequest>> entity = testRestTemplate.exchange(
                "/requests/waiter-or-barmen",
                HttpMethod.GET,
                tokenUtil.createWaiterAuthEntity(null),
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotEmpty()
                .hasSize(1)
                .contains(clientRequestSaved);
    }

    @Test
    @DisplayName("listAll return empty page when there are no client requests")
    void listAll_ReturnListOfClientRequestsInsidePageObject_WhenThereAreNoClientRequests() {
        ResponseEntity<PageableResponse<ClientRequest>> entity = testRestTemplate.exchange(
                "/requests/waiter-or-barmen",
                HttpMethod.GET,
                tokenUtil.createWaiterAuthEntity(null),
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
    @DisplayName("listAll returns 403 Forbidden when user does not have ROLE_WAITER or ROLE_BARMEN7")
    void listAll_Returns403Forbidden_WhenUserDoesNotHaveROLE_WAITERorROLE_BARMEN() {
        ResponseEntity<Void> entity = testRestTemplate.exchange(
                "/requests/waiter-or-barmen",
                HttpMethod.GET,
                tokenUtil.createUserAuthEntity(null),
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("findById returns an client request object when successful")
    void findById_ReturnsClientRequestObject_WhenSuccessful() {
        ClientRequest clientRequestSaved = persistClientRequest();

        ResponseEntity<ClientRequest> entity = testRestTemplate.exchange(
                "/requests/{uuid}",
                HttpMethod.GET,
                null,
                ClientRequest.class,
                clientRequestSaved.getUuid()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(clientRequestSaved);
    }

    @Test
    @DisplayName("findById returns 400 BadRequest when client request not exists")
    void findById_Returns400BadRequest_WhenClientRequestNotExists() {
        ResponseEntity<ClientRequest> entity = testRestTemplate.exchange(
                "/requests/{uuid}",
                HttpMethod.GET,
                null,
                ClientRequest.class,
                UUID.randomUUID()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("search return list of client requests inside page object when successful")
    void search_ReturnListOfClientRequestsInsidePageObject_WhenSuccessful() {
        ClientRequest clientRequestSaved = persistClientRequest();

        String url = String.format("/requests/all/search?drinkUUID=%s", clientRequestSaved.getDrinks().get(0).getUuid());

        ResponseEntity<PageableResponse<ClientRequest>> entity = testRestTemplate.exchange(
                url,
                HttpMethod.GET,
                tokenUtil.createWaiterAuthEntity(null),
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotEmpty()
                .hasSize(1)
                .contains(clientRequestSaved);
    }

    @Test
    @DisplayName("search return empty page object when does not match")
    void search_ReturnEmptyPage_WhenDoesNotMatch() {
        String url = String.format("/requests/all/search?drinkUUID=%s", UUID.randomUUID());

        ResponseEntity<PageableResponse<ClientRequest>> entity = testRestTemplate.exchange(
                url,
                HttpMethod.GET,
                tokenUtil.createWaiterAuthEntity(null),
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
    @DisplayName("save creates client request when successful")
    void save_CreatesClientRequest_WhenSuccessful() {
        Drink drinkSaved = drinkRepository.save(DrinkCreator.createDrinkToBeSave());

        Table tableSaved = tableRepository.save(TableCreator.createTableToBeSave());

        ClientRequestPostRequestBody clientRequestValid = ClientRequestPostRequestBodyCreator.createClientRequestPostRequestBodyToBeSave();

        clientRequestValid.setDrinks(new ArrayList<>(List.of(drinkSaved)));

        clientRequestValid.setTable(tableSaved);

        ResponseEntity<ClientRequest> entity = testRestTemplate.postForEntity(
                "/requests/user",
                tokenUtil.createUserAuthEntity(clientRequestValid),
                ClientRequest.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getUuid()).isNotNull();

        assertThat(entity.getBody().getDrinks()).isNotEmpty();

        assertThat(entity.getBody().getDrinks().get(0)).isEqualTo(clientRequestValid.getDrinks().get(0));
    }

    @Test
    @DisplayName("save returns 400 BadRequest when user is minor and tries to buy an alcoholic drink")
    void save_Returns400BadRequest_WhenUserIsMinorAndTriesToBuyAnAlcoholicDrink() {
        Drink drinkToBeSave = DrinkCreator.createDrinkToBeSave();

        drinkToBeSave.setAlcoholic(true);

        Drink drinkSaved = drinkRepository.save(drinkToBeSave);

        Table tableSaved = tableRepository.save(TableCreator.createTableToBeSave());

        ClientRequestPostRequestBody clientRequestValid = ClientRequestPostRequestBodyCreator.createClientRequestPostRequestBodyToBeSave();

        clientRequestValid.setDrinks(new ArrayList<>(List.of(drinkSaved)));

        clientRequestValid.setTable(tableSaved);

        ResponseEntity<ClientRequest> entity = testRestTemplate.postForEntity(
                "/requests/user",
                tokenUtil.createUserMinorAuthEntity(clientRequestValid),
                ClientRequest.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("save returns 403 Forbidden when user does not have ROLE_USER")
    void save_Returns403Forbidden_WhenUserDoesNotHaveROLE_USER() {
        Drink drinkSaved = drinkRepository.save(DrinkCreator.createDrinkToBeSave());

        Table tableSaved = tableRepository.save(TableCreator.createTableToBeSave());

        ClientRequestPostRequestBody clientRequestValid = ClientRequestPostRequestBodyCreator.createClientRequestPostRequestBodyToBeSave();

        clientRequestValid.setDrinks(new ArrayList<>(List.of(drinkSaved)));

        clientRequestValid.setTable(tableSaved);

        ResponseEntity<ClientRequest> entity = testRestTemplate.postForEntity(
                "/requests/user",
                tokenUtil.createBarmenAuthEntity(clientRequestValid),
                ClientRequest.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("replace updates client request when successful")
    void replace_UpdatedClientRequest_WhenSuccessful() {
        ClientRequest clientRequestSaved = persistClientRequest();

        ClientRequestPutRequestBody clientToUpdate = ClientRequestPutRequestBodyCreator.createClientRequestPutRequestBodyCreatorToBeUpdate();

        clientToUpdate.setUuid(clientRequestSaved.getUuid());

        clientToUpdate.setDrinks(clientRequestSaved.getDrinks());

        clientToUpdate.setTable(clientRequestSaved.getTable());

        clientToUpdate.setUser(clientRequestSaved.getUser());

        ResponseEntity<Void> entity = testRestTemplate.exchange(
                "/requests/all",
                HttpMethod.PUT,
                tokenUtil.createWaiterAuthEntity(clientToUpdate),
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("replace returns 400 BadRequest when client request not exists")
    void replace_Returns400BadRequest_WhenClientRequestNotExists() {
        ClientRequestPutRequestBody clientToUpdate = ClientRequestPutRequestBodyCreator.createClientRequestPutRequestBodyCreatorToBeUpdate();

        ResponseEntity<Void> entity = testRestTemplate.exchange(
                "/requests/all",
                HttpMethod.PUT,
                tokenUtil.createWaiterAuthEntity(clientToUpdate),
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("replace returns 400 BadRequest when user is not staff or owner of request")
    void replace_Returns400BadRequest_WhenUserIsNotStaffOrOwnerOfRequest() {
        ClientRequest clientRequestSaved = persistClientRequest();

        ClientRequestPutRequestBody clientToUpdate = ClientRequestPutRequestBodyCreator.createClientRequestPutRequestBodyCreatorToBeUpdate();

        clientToUpdate.setUuid(clientRequestSaved.getUuid());

        clientToUpdate.setDrinks(clientRequestSaved.getDrinks());

        clientToUpdate.setTable(clientRequestSaved.getTable());

        clientToUpdate.setUser(clientRequestSaved.getUser());

        ResponseEntity<Void> entity = testRestTemplate.exchange(
                "/requests/all",
                HttpMethod.PUT,
                tokenUtil.createUserAuthEntity(clientToUpdate),
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("finishRequest finish client request when successful")
    void finishRequest_FinishClientRequest_WhenSuccessful() {
        ClientRequest clientRequestSaved = persistClientRequest();

        ResponseEntity<ClientRequest> entity = testRestTemplate.exchange(
                "/requests/all/{uuid}",
                HttpMethod.PATCH,
                tokenUtil.createWaiterAuthEntity(null),
                ClientRequest.class,
                clientRequestSaved.getUuid()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getUuid())
                .isNotNull()
                .isEqualTo(clientRequestSaved.getUuid());

        assertThat(entity.getBody().isFinished()).isNotEqualTo(clientRequestSaved.isFinished());

        assertThat(entity.getBody().getTotalPrice()).isNotEqualTo(clientRequestSaved.getTotalPrice());
    }

    @Test
    @DisplayName("finishRequest returns 400 BadRequest when client request already finished")
    void finishRequest_Returns400BadRequest_WhenClientRequestAlreadyFinished() {
        ClientRequest clientRequestSaved = persistClientRequest();

        clientRequestSaved.setFinished(true);

        ClientRequest clientRequestFinished = clientRequestRepository.save(clientRequestSaved);

        ResponseEntity<ClientRequest> entity = testRestTemplate.exchange(
                "/requests/all/{uuid}",
                HttpMethod.PATCH,
                tokenUtil.createWaiterAuthEntity(null),
                ClientRequest.class,
                clientRequestFinished.getUuid()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("finishRequest returns 400 BadRequest when client request not exists")
    void finishRequest_Returns400BadRequest_WhenClientRequestNotExists() {
        ResponseEntity<ClientRequest> entity = testRestTemplate.exchange(
                "/requests/all/{uuid}",
                HttpMethod.PATCH,
                tokenUtil.createWaiterAuthEntity(null),
                ClientRequest.class,
                UUID.randomUUID()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("finishRequest returns 400 BadRequest when user is not staff or owner of request")
    void finishRequest_Returns400BadRequest_WhenUserIsNotStaffOrOwnerOfRequest() {
        ClientRequest clientRequestSaved = persistClientRequest();

        ClientRequest clientRequestFinished = clientRequestRepository.save(clientRequestSaved);

        ResponseEntity<ClientRequest> entity = testRestTemplate.exchange(
                "/requests/all/{uuid}",
                HttpMethod.PATCH,
                tokenUtil.createUserAuthEntity(null),
                ClientRequest.class,
                clientRequestFinished.getUuid()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("delete removes client request when successful")
    void delete_RemovesClientRequest_WhenSuccessful() {
        ClientRequest clientRequestSaved = persistClientRequest();

        ResponseEntity<Void> entity = testRestTemplate.exchange(
                "/requests/all/{uuid}",
                HttpMethod.DELETE,
                tokenUtil.createWaiterAuthEntity(null),
                Void.class,
                clientRequestSaved.getUuid()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete returns 400 BadRequest when client request not exists")
    void delete_Returns400BadRequest_WhenClientRequestNotExists() {
        ResponseEntity<Void> entity = testRestTemplate.exchange(
                "/requests/all/{uuid}",
                HttpMethod.DELETE,
                tokenUtil.createWaiterAuthEntity(null),
                Void.class,
                UUID.randomUUID()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("delete returns 400 BadRequest when user is not staff or owner of request")
    void delete_Returns400BadRequest_WhenUserIsNotStaffOrOwnerOfRequest() {
        ClientRequest clientRequestSaved = persistClientRequest();

        ResponseEntity<Void> entity = testRestTemplate.exchange(
                "/requests/all/{uuid}",
                HttpMethod.DELETE,
                tokenUtil.createUserAuthEntity(null),
                Void.class,
                clientRequestSaved.getUuid()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private ClientRequest persistClientRequest() {
        Drink drinkSaved = drinkRepository.save(DrinkCreator.createDrinkToBeSave());

        Table tableSaved = tableRepository.save(TableCreator.createTableToBeSave());

        ApplicationUser userSaved = applicationUserRepository.save(ApplicationUserCreator.createApplicationUserToBeSave());

        ClientRequest clientRequestValid = ClientRequestCreator.createClientRequestToBeSave();

        clientRequestValid.setDrinks(new ArrayList<>(List.of(drinkSaved)));

        clientRequestValid.setUser(userSaved);

        clientRequestValid.setTable(tableSaved);

        return clientRequestRepository.save(clientRequestValid);
    }

}
