package com.github.skyg0d.skydrinksapi.integration;

import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.domain.Table;
import com.github.skyg0d.skydrinksapi.repository.table.TableRepository;
import com.github.skyg0d.skydrinksapi.requests.TablePostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.TablePutRequestBody;
import com.github.skyg0d.skydrinksapi.util.table.TableCreator;
import com.github.skyg0d.skydrinksapi.util.table.TablePostRequestBodyCreator;
import com.github.skyg0d.skydrinksapi.util.table.TablePutRequestBodyCreator;
import com.github.skyg0d.skydrinksapi.wrapper.PageableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Integration Tests for TableController")
class TableControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private TableRepository tableRepository;

    @Test
    @DisplayName("listAll return list of tables inside page object when successful")
    void listAll_ReturnListOfTablesInsidePageObject_WhenSuccessful() {
        Table tableSaved = tableRepository.save(TableCreator.createTableToBeSave());

        ResponseEntity<PageableResponse<Table>> entity = testRestTemplate.exchange(
                "/tables",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotEmpty()
                .hasSize(1)
                .contains(tableSaved);
    }

    @Test
    @DisplayName("listAll return empty page when there are no tables")
    void listAll_ReturnEmptyPage_WhenThereAreNoTables() {
        ResponseEntity<PageableResponse<Table>> entity = testRestTemplate.exchange(
                "/tables",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isEmpty();
    }

    @Test
    @DisplayName("findById returns an table object when successful")
    void findById_ReturnsAnTableObject_WhenSuccessful() {
        Table tableSaved = tableRepository.save(TableCreator.createTableToBeSave());

        ResponseEntity<Table> entity = testRestTemplate.getForEntity("/tables/{uuid}", Table.class, tableSaved.getUuid());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(tableSaved);
    }

    @Test
    @DisplayName("findById returns 400 BadRequest when table not exists")
    void findById_Returns400BadRequest_WhenTableNotExists() {
        ResponseEntity<Drink> entity = testRestTemplate.getForEntity("/tables/{uuid}", Drink.class, UUID.randomUUID());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("findByNumber returns an table object when successful")
    void findByNumber_ReturnsAnTableObject_WhenSuccessful() {
        Table tableSaved = tableRepository.save(TableCreator.createTableToBeSave());

        ResponseEntity<Table> entity = testRestTemplate.getForEntity("/tables/find-by-number/{number}", Table.class, tableSaved.getNumber());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(tableSaved);
    }

    @Test
    @DisplayName("findByNumber returns 400 BadRequest when table not exists")
    void findByNumber_Returns400BadRequest_WhenTableNotExists() {
        ResponseEntity<Table> entity = testRestTemplate.getForEntity("/tables/find-by-number/{number}", Table.class, -1);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    @DisplayName("search return list of tables inside page object when successful")
    void search_ReturnListOfTablesInsidePageObject_WhenSuccessful() {
        Table tableSaved = tableRepository.save(TableCreator.createTableToBeSave());

        String url = String.format("/tables/search?seats=%d", tableSaved.getSeats());

        ResponseEntity<PageableResponse<Table>> entity = testRestTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotEmpty()
                .hasSize(1)
                .contains(tableSaved);
    }

    @Test
    @DisplayName("search return empty page object when does not match")
    void search_ReturnEmptyPage_WhenDoesNotMatch() {
        tableRepository.save(TableCreator.createTableToBeSave());

        String url = String.format("/tables/search?seats=%d", 12);

        ResponseEntity<PageableResponse<Table>> entity = testRestTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
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
    @DisplayName("save creates table when successful")
    void save_CreatesTable_WhenSuccessful() {
        TablePostRequestBody tableValid = TablePostRequestBodyCreator.createTablePostRequestBodyToBeSave();

        ResponseEntity<Table> entity = testRestTemplate.postForEntity(
                "/tables",
                tableValid,
                Table.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getUuid()).isNotNull();

        assertThat(entity.getBody().getNumber()).isEqualTo(tableValid.getNumber());
    }

    @Test
    @DisplayName("replace updates table when successful")
    void replace_UpdatesTable_WhenSuccessful() {
        Table tableSaved = tableRepository.save(TableCreator.createTableToBeSave());

        TablePutRequestBody tableToUpdate = TablePutRequestBodyCreator.createTablePutRequestBodyToUpdate();

        tableToUpdate.setUuid(tableSaved.getUuid());

        ResponseEntity<Void> entity = testRestTemplate.exchange(
                "/tables",
                HttpMethod.PUT,
                new HttpEntity<>(tableToUpdate),
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("replace returns 400 BadRequest when table not exists")
    void replace_Returns400BadRequest_WhenTableNotExists() {
        TablePutRequestBody tableToUpdate = TablePutRequestBodyCreator.createTablePutRequestBodyToUpdate();

        ResponseEntity<Void> entity = testRestTemplate.exchange(
                "/tables",
                HttpMethod.PUT,
                new HttpEntity<>(tableToUpdate),
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    @DisplayName("switchOccupied updates table with table number when successful")
    void switchOccupied_UpdatesTableWithTableNumber_WhenSuccessful() {
        Table tableSaved = tableRepository.save(TableCreator.createTableToBeSave());

        ResponseEntity<Table> entity = testRestTemplate.exchange(
                "/tables/switch-occupied/{identification}",
                HttpMethod.PATCH,
                null,
                Table.class,
                tableSaved.getNumber()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().isOccupied()).isNotEqualTo(tableSaved.isOccupied());
    }

    @Test
    @DisplayName("switchOccupied updates table with table UUID when successful")
    void switchOccupied_UpdatesTableWithTableUUID_WhenSuccessful() {
        Table tableSaved = tableRepository.save(TableCreator.createTableToBeSave());

        ResponseEntity<Table> entity = testRestTemplate.exchange(
                "/tables/switch-occupied/{identification}",
                HttpMethod.PATCH,
                null,
                Table.class,
                tableSaved.getUuid()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().isOccupied()).isNotEqualTo(tableSaved.isOccupied());
    }

    @Test
    @DisplayName("switchOccupied returns 400 BadRequest when table not exists")
    void switchOccupied_Returns400BadRequest_WhenTableNotExists() {
        ResponseEntity<Table> entity = testRestTemplate.exchange(
                "/tables/switch-occupied/{identification}",
                HttpMethod.PATCH,
                null,
                Table.class,
                1
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("delete removes table when successful")
    void delete_RemovesTable_WhenSuccessful() {
        Table tableSaved = tableRepository.save(TableCreator.createTableToBeSave());

        ResponseEntity<Void> entity = testRestTemplate.exchange(
                "/tables/{uuid}",
                HttpMethod.DELETE,
                null,
                Void.class,
                tableSaved.getUuid()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete returns 400 BadRequest when table not exists")
    void delete_Returns400BadRequest_WhenTableNotExists() {
        ResponseEntity<Void> entity = testRestTemplate.exchange(
                "/tables/{uuid}",
                HttpMethod.DELETE,
                null,
                Void.class,
                UUID.randomUUID()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

}
