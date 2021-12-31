package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.domain.Table;
import com.github.skyg0d.skydrinksapi.exception.BadRequestException;
import com.github.skyg0d.skydrinksapi.parameters.TableParameters;
import com.github.skyg0d.skydrinksapi.repository.request.ClientRequestRepository;
import com.github.skyg0d.skydrinksapi.repository.table.TableRepository;
import com.github.skyg0d.skydrinksapi.util.UUIDUtil;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for TableService")
class TableServiceTest {

    @InjectMocks
    private TableService tableService;

    @Mock
    private TableRepository tableRepositoryMock;

    @Mock
    private ClientRequestRepository clientRequestRepository;

    @Mock
    private UUIDUtil uuidUtilMock;

    @BeforeEach
    void setUp() {
        Page<Table> tablePage = new PageImpl<>(List.of(TableCreator.createValidTable()));

        BDDMockito
                .when(tableRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(tablePage);

        BDDMockito
                .when(tableRepositoryMock.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(TableCreator.createValidTable()));

        BDDMockito
                .when(tableRepositoryMock.findByNumber(ArgumentMatchers.anyInt()))
                .thenReturn(Optional.of(TableCreator.createValidTable()));

        BDDMockito
                .when(tableRepositoryMock.findAll(ArgumentMatchers.<Specification<Table>>any(), ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(tablePage);

        BDDMockito
                .when(tableRepositoryMock.save(ArgumentMatchers.any(Table.class)))
                .thenReturn(TableCreator.createValidTable());

        BDDMockito
                .doNothing()
                .when(tableRepositoryMock)
                .delete(ArgumentMatchers.any(Table.class));

        BDDMockito
                .when(uuidUtilMock.getUUID(ArgumentMatchers.anyString()))
                .thenReturn(null);
    }

    @Test
    @DisplayName("listAll return list of tables inside page object when successful")
    void listAll_ReturnListOfTablesInsidePageObject_WhenSuccessful() {
        Table expectedTable = TableCreator.createValidTable();

        Page<Table> drinkPage = tableService.listAll(PageRequest.of(1, 1));

        assertThat(drinkPage).isNotNull();

        assertThat(drinkPage.toList())
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedTable);
    }

    @Test
    @DisplayName("listAll return empty page when there are no tables")
    void listAll_ReturnListOfTablesInsidePageObject_WhenThereAreNoTables() {
        BDDMockito
                .when(tableRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(Page.empty());

        Page<Table> drinkPage = tableService.listAll(PageRequest.of(1, 1));

        assertThat(drinkPage).isEmpty();
    }

    @Test
    @DisplayName("findByIdOrElseThrowBadRequestException returns an table object when successful")
    void findByIdOrElseThrowBadRequestException_ReturnsAnTableObject_WhenSuccessful() {
        Table expectedTable = TableCreator.createValidTable();

        Table drinkFound = tableService.findByIdOrElseThrowBadRequestException(UUID.randomUUID());

        assertThat(drinkFound)
                .isNotNull()
                .isEqualTo(expectedTable);
    }

    @Test
    @DisplayName("findByNumberOrElseThrowBadRequestException returns an table object when successful")
    void findByNumberOrElseThrowBadRequestException_ReturnsAnTableObject_WhenSuccessful() {
        Table expectedTable = TableCreator.createValidTable();

        Table drinkFound = tableService.findByNumberOrElseThrowBadRequestException(0);

        assertThat(drinkFound)
                .isNotNull()
                .isEqualTo(expectedTable);
    }

    @Test
    @DisplayName("search return list of tables inside page object when successful")
    void search_ReturnListOfTablesInsidePageObject_WhenSuccessful() {
        Table expectedTable = TableCreator.createValidTable();

        Page<Table> drinkPage = tableService.search(new TableParameters(), PageRequest.of(1, 1));

        assertThat(drinkPage).isNotNull();

        assertThat(drinkPage.toList())
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedTable);
    }

    @Test
    @DisplayName("save creates table when successful")
    void save_CreatesTable_WhenSuccessful() {
        BDDMockito
                .when(tableRepositoryMock.findByNumber(ArgumentMatchers.anyInt()))
                .thenReturn(Optional.empty());

        Table expectedTable = TableCreator.createValidTable();

        Table drinkSaved = tableService.save(TablePostRequestBodyCreator.createTablePostRequestBodyToBeSave());

        assertThat(drinkSaved)
                .isNotNull()
                .isEqualTo(expectedTable);
    }

    @Test
    @DisplayName("replace updates table when successful")
    void replace_UpdatedTable_WhenSuccessful() {
        BDDMockito
                .when(tableRepositoryMock.save(ArgumentMatchers.any(Table.class)))
                .thenReturn(TableCreator.createValidUpdatedTable());


        assertThatCode(() -> tableService.replace(TablePutRequestBodyCreator.createTablePutRequestBodyToUpdate()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("switchOccupied updates table when successful")
    void switchOccupied_UpdatesTableWithTableNumber_WhenSuccessful() {
        BDDMockito
                .when(tableRepositoryMock.save(ArgumentMatchers.any(Table.class)))
                .thenReturn(TableCreator.createValidSwitchedTable());

        Table expectedTable = TableCreator.createValidSwitchedTable();

        String identification = "1";

        Table tableSwitched = tableService.switchOccupied(identification);

        assertThat(tableSwitched).isNotNull();

        assertThat(tableSwitched.isOccupied()).isEqualTo(expectedTable.isOccupied());
    }

    @Test
    @DisplayName("switchOccupied updates table when successful")
    void switchOccupied_UpdatesTableWithTableUUID_WhenSuccessful() {
        BDDMockito.when(uuidUtilMock.getUUID(ArgumentMatchers.anyString()))
                        .thenReturn(UUID.randomUUID());

        BDDMockito
                .when(tableRepositoryMock.save(ArgumentMatchers.any(Table.class)))
                .thenReturn(TableCreator.createValidSwitchedTable());

        Table expectedTable = TableCreator.createValidSwitchedTable();

        String identification = UUID.randomUUID().toString();

        Table tableSwitched = tableService.switchOccupied(identification);

        assertThat(tableSwitched).isNotNull();

        assertThat(tableSwitched.isOccupied()).isEqualTo(expectedTable.isOccupied());
    }

    @Test
    @DisplayName("delete removes table when successful")
    void delete_RemovesTable_WhenSuccessful() {
        assertThatCode(() -> tableService.delete(UUID.randomUUID()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("findByIdOrElseThrowBadRequestException throws BadRequestException when table is not found")
    void findByIdOrElseThrowBadRequestException_ThrowsBadRequestException_WhenTableIsNotFound() {
        BDDMockito
                .when(tableRepositoryMock.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> tableService.findByIdOrElseThrowBadRequestException(UUID.randomUUID()));
    }

    @Test
    @DisplayName("findByNumberOrElseThrowBadRequestException throws BadRequestException when table is not found")
    void findByNumberOrElseThrowBadRequestException_ThrowsBadRequestException_WhenTableIsNotFound() {
        BDDMockito
                .when(tableRepositoryMock.findByNumber(ArgumentMatchers.anyInt()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> tableService.findByNumberOrElseThrowBadRequestException(0));
    }

    @Test
    @DisplayName("save throws BadRequestException when number of table already exists")
    void save_ThrowsBadRequestException_WhenNumberOfTableAlreadyExists() {
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> tableService.save(TablePostRequestBodyCreator.createTablePostRequestBodyToBeSave()));
    }

}
