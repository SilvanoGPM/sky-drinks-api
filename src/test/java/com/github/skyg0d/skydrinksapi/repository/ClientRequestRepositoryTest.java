package com.github.skyg0d.skydrinksapi.repository;

import com.github.skyg0d.skydrinksapi.domain.*;
import com.github.skyg0d.skydrinksapi.enums.ClientRequestStatus;
import com.github.skyg0d.skydrinksapi.repository.drink.DrinkRepository;
import com.github.skyg0d.skydrinksapi.repository.request.ClientRequestRepository;
import com.github.skyg0d.skydrinksapi.repository.table.TableRepository;
import com.github.skyg0d.skydrinksapi.repository.user.ApplicationUserRepository;
import com.github.skyg0d.skydrinksapi.util.drink.DrinkCreator;
import com.github.skyg0d.skydrinksapi.util.request.ClientRequestCreator;
import com.github.skyg0d.skydrinksapi.util.table.TableCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@DisplayName("Tests for ClienteRequestRepository")
class ClientRequestRepositoryTest {

    @Autowired
    private ClientRequestRepository clientRequestRepository;

    @Autowired
    private DrinkRepository drinkRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private TableRepository tableRepository;

    @Test
    @DisplayName("save persist client request when successful")
    void save_PersistClientRequest_WhenSuccessful() {
        ClientRequest requestToBeSave = ClientRequestCreator.createClientRequestToBeSave();

        ClientRequest requestSaved = clientRequestRepository.save(requestToBeSave);

        assertThat(requestSaved).isNotNull();

        assertThat(requestSaved.getTotalPrice())
                .isNotNull()
                .isEqualTo(requestToBeSave.getTotalPrice());

        assertThat(requestSaved.getDrinks())
                .isNotEmpty()
                .contains(DrinkCreator.createValidDrink());

        assertThat(requestSaved.getTable())
                .isNotNull()
                .isEqualTo(TableCreator.createValidTable());
    }

    @Test
    @DisplayName("save updates client request when successful")
    void save_UpdateClientRequest_WhenSuccessful() {
        Drink drinkSaved = drinkRepository.save(DrinkCreator.createDrinkToBeSave());

        Table tableSaved = tableRepository.save(TableCreator.createTableToBeSave());

        ClientRequest requestToBeSave = ClientRequestCreator.createClientRequestToBeSave();

        requestToBeSave.setDrinks(new ArrayList<>(List.of(drinkSaved)));

        requestToBeSave.setTable(tableSaved);

        ClientRequest requestSaved = clientRequestRepository.save(requestToBeSave);

        requestSaved.setTotalPrice(DrinkCreator.createValidDrink().getPrice());
        requestSaved.setStatus(ClientRequestStatus.FINISHED);

        ClientRequest requestUpdated = clientRequestRepository.save(requestSaved);

        assertThat(requestUpdated).isNotNull();

        assertThat(requestUpdated.getStatus()).isEqualTo(requestSaved.getStatus());

        assertThat(requestUpdated.getTotalPrice()).isEqualTo(requestSaved.getTotalPrice());
    }

    @Test
    @DisplayName("delete remove client request when successful")
    void delete_RemoveClientRequest_WhenSuccessful() {
        ClientRequest requestToBeSave = ClientRequestCreator.createClientRequestToBeSave();

        List<Drink> drinksSaved = drinkRepository.saveAll(requestToBeSave.getDrinks());

        ClientRequest requestSaved = clientRequestRepository.save(requestToBeSave);

        requestSaved.setDrinks(drinksSaved);

        clientRequestRepository.delete(requestSaved);

        Optional<ClientRequest> requestFound = clientRequestRepository.findById(requestSaved.getUuid());

        assertThat(requestFound).isEmpty();
    }

    @Test
    @DisplayName("countTotalDrinksInRequest returns client request drinks count when successful")
    void countTotalDrinksInRequest_ReturnsClientRequestDrinksCount_WhenSuccessful() {
        ClientRequest requestToBeSave = ClientRequestCreator.createClientRequestToBeSave();

        ApplicationUser userSaved = applicationUserRepository.save(requestToBeSave.getUser());

        List<Drink> drinksSaved = drinkRepository.saveAll(requestToBeSave.getDrinks());

        Table tableSaved = tableRepository.save(requestToBeSave.getTable());

        requestToBeSave.setUser(userSaved);
        requestToBeSave.setDrinks(drinksSaved);
        requestToBeSave.setTable(tableSaved);

        clientRequestRepository.save(requestToBeSave);

        List<ClientRequestDrinkCount> drinksFound = clientRequestRepository.countTotalDrinksInRequest(userSaved.getUuid(), PageRequest.of(0, 1));

        assertThat(drinksFound)
                .isNotEmpty()
                .hasSize(1);

        assertThat(drinksFound.get(0)).isNotNull();

        assertThat(drinksFound.get(0).getDrinkUUID())
                .isNotNull()
                .isEqualTo(drinksSaved.get(0).getUuid());
    }

    @Test
    @DisplayName("countAlcoholicDrinksInRequests returns total of client requests grouped by alcoholic when successful")
    void countAlcoholicDrinksInRequests_ReturnsTotalOfClientRequestsGroupedByAlcoholic_WhenSuccessful() {
        ClientRequest requestToBeSave = ClientRequestCreator.createClientRequestToBeSave();

        ApplicationUser userSaved = applicationUserRepository.save(requestToBeSave.getUser());

        List<Drink> drinksSaved = drinkRepository.saveAll(requestToBeSave.getDrinks());

        Table tableSaved = tableRepository.save(requestToBeSave.getTable());

        requestToBeSave.setUser(userSaved);
        requestToBeSave.setDrinks(drinksSaved);
        requestToBeSave.setTable(tableSaved);

        clientRequestRepository.save(requestToBeSave);

        List<ClientRequestAlcoholicDrinkCount> drinksFound = clientRequestRepository.countAlcoholicDrinksInRequests(userSaved.getUuid(), PageRequest.of(0, 2));

        assertThat(drinksFound)
                .isNotEmpty()
                .hasSize(1);

        assertThat(drinksFound.get(0)).isNotNull();

        assertThat(drinksFound.get(0).isAlcoholic()).isFalse();

        assertThat(drinksFound.get(0).getTotal()).isEqualTo(1);
    }

    @Test
    @DisplayName("getAllDatesInRequests returns all dates in requests when successful")
    void getAllDatesInRequests_ReturnsAllDatesInRequests_WhenSuccessful() {
        ClientRequest requestToBeSave = ClientRequestCreator.createClientRequestToBeSave();

        ApplicationUser userSaved = applicationUserRepository.save(requestToBeSave.getUser());

        List<Drink> drinksSaved = drinkRepository.saveAll(requestToBeSave.getDrinks());

        Table tableSaved = tableRepository.save(requestToBeSave.getTable());

        requestToBeSave.setUser(userSaved);
        requestToBeSave.setDrinks(drinksSaved);
        requestToBeSave.setTable(tableSaved);

        ClientRequest requestSaved = clientRequestRepository.save(requestToBeSave);

        List<ClientRequestDate> datesFound = clientRequestRepository.getAllDatesInRequests();

        assertThat(datesFound)
                .isNotEmpty()
                .hasSize(1);

        assertThat(datesFound.get(0)).isNotNull();

        assertThat(datesFound.get(0).getDate())
                .isNotNull()
                .isEqualTo(requestSaved.getCreatedAt().toLocalDate());
    }

    @Test
    @DisplayName("save throws ConstraintViolationException when client request properties is invalid")
    void save_ThrowsConstraintViolationException_WhenClientRequestPropertiesIsInvalid() {
        ClientRequest request = new ClientRequest();

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> clientRequestRepository.saveAndFlush(request));

    }

}