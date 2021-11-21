package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.domain.Table;
import com.github.skyg0d.skydrinksapi.parameters.TableParameters;
import com.github.skyg0d.skydrinksapi.requests.TablePostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.TablePutRequestBody;
import com.github.skyg0d.skydrinksapi.service.TableService;
import com.github.skyg0d.skydrinksapi.util.table.TableCreator;
import com.github.skyg0d.skydrinksapi.util.table.TablePostRequestBodyCreator;
import com.github.skyg0d.skydrinksapi.util.table.TablePutRequestBodyCreator;
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
@DisplayName("Tests for TableController")
class TableControllerTest {

    @InjectMocks
    private TableController tableController;

    @Mock
    private TableService tableServiceMock;

    @BeforeEach
    void setUp() {
        Page<Table> tablePage = new PageImpl<>(List.of(TableCreator.createValidTable()));

        BDDMockito
                .when(tableServiceMock.listAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(tablePage);

        BDDMockito
                .when(tableServiceMock.findByIdOrElseThrowBadRequestException(ArgumentMatchers.any(UUID.class)))
                .thenReturn(TableCreator.createValidTable());

        BDDMockito
                .when(tableServiceMock.findByNumberOrElseThrowBadRequestException(ArgumentMatchers.anyInt()))
                .thenReturn(TableCreator.createValidTable());

        BDDMockito
                .when(tableServiceMock.search(ArgumentMatchers.any(TableParameters.class), ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(tablePage);

        BDDMockito
                .when(tableServiceMock.save(ArgumentMatchers.any(TablePostRequestBody.class)))
                .thenReturn(TableCreator.createValidTable());

        BDDMockito
                .doNothing()
                .when(tableServiceMock)
                .replace(ArgumentMatchers.any(TablePutRequestBody.class));

        BDDMockito
                .when(tableServiceMock.switchOccupied(ArgumentMatchers.anyString()))
                .thenReturn(TableCreator.createValidSwitchedTable());

        BDDMockito
                .doNothing()
                .when(tableServiceMock)
                .delete(ArgumentMatchers.any(UUID.class));
    }

    @Test
    @DisplayName("listAll return list of tables inside page object when successful")
    void listAll_ReturnListOfTablesInsidePageObject_WhenSuccessful() {
        Table expectedTable = TableCreator.createValidTable();

        ResponseEntity<Page<Table>> entity = tableController.listAll(PageRequest.of(1, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedTable);
    }

    @Test
    @DisplayName("listAll return empty page when there are no tables")
    void listAll_ReturnListOfTablesInsidePageObject_WhenThereAreNoTables() {
        BDDMockito
                .when(tableServiceMock.listAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(Page.empty());

        ResponseEntity<Page<Table>> entity = tableController.listAll(PageRequest.of(1, 1));

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
        Table expectedTable = TableCreator.createValidTable();

        ResponseEntity<Table> entity = tableController.findById(UUID.randomUUID());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedTable);
    }

    @Test
    @DisplayName("findByNumber returns an table object when successful")
    void findByNumber_ReturnsAnTableObject_WhenSuccessful() {
        Table expectedTable = TableCreator.createValidTable();

        ResponseEntity<Table> entity = tableController.findByNumber(0);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedTable);
    }

    @Test
    @DisplayName("search return list of tables inside page object when successful")
    void search_ReturnListOfTablesInsidePageObject_WhenSuccessful() {
        Table expectedTable = TableCreator.createValidTable();

        ResponseEntity<Page<Table>> entity = tableController.search(new TableParameters(), PageRequest.of(1, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedTable);
    }

    @Test
    @DisplayName("save creates table when successful")
    void save_CreatesTable_WhenSuccessful() {
        Table expectedTable = TableCreator.createValidTable();

        ResponseEntity<Table> entity = tableController.save(TablePostRequestBodyCreator.createTablePostRequestBodyToBeSave());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedTable);
    }

    @Test
    @DisplayName("replace updates table when successful")
    void replace_UpdatedTable_WhenSuccessful() {
        ResponseEntity<Void> entity = tableController.replace(TablePutRequestBodyCreator.createTablePutRequestBodyToUpdate());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("switchOccupied updates table when successful")
    void switchOccupied_UpdatesTableWithTableNumber_WhenSuccessful() {
        Table expectedTable = TableCreator.createValidSwitchedTable();

        String identification = "1";

        ResponseEntity<Table> entity = tableController.switchOccupied(identification);

        assertThat(entity).isNotNull();

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().isOccupied()).isEqualTo(expectedTable.isOccupied());
    }

    @Test
    @DisplayName("switchOccupied updates table when successful")
    void switchOccupied_UpdatesTableWithTableUUID_WhenSuccessful() {
        Table expectedTable = TableCreator.createValidSwitchedTable();

        String identification = UUID.randomUUID().toString();

        ResponseEntity<Table> entity = tableController.switchOccupied(identification);

        assertThat(entity).isNotNull();

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().isOccupied()).isEqualTo(expectedTable.isOccupied());
    }

    @Test
    @DisplayName("delete removes table when successful")
    void delete_RemovesTable_WhenSuccessful() {
        ResponseEntity<Void> entity = tableController.delete(UUID.randomUUID());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

}