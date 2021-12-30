package com.github.skyg0d.skydrinksapi.repository;

import com.github.skyg0d.skydrinksapi.domain.Table;
import com.github.skyg0d.skydrinksapi.repository.table.TableRepository;
import com.github.skyg0d.skydrinksapi.util.table.TableCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
@DisplayName("Tests for TableRepository")
class TableRepositoryTest {

    @Autowired
    private TableRepository tableRepository;

    @Test
    @DisplayName("save persist table when successful")
    void save_PersistTable_WhenSuccessful() {
        Table tableToBeSave = TableCreator.createTableToBeSave();

        Table tableSaved = tableRepository.save(tableToBeSave);

        assertThat(tableSaved).isNotNull();

        assertThat(tableSaved.getNumber())
                .isNotNull()
                .isEqualTo(tableToBeSave.getNumber());

        assertThat(tableSaved.isOccupied()).isEqualTo(tableToBeSave.isOccupied());
    }

    @Test
    @DisplayName("save updates table when successful")
    void save_UpdateTable_WhenSuccessful() {
        Table tableToBeSave = TableCreator.createTableToBeSave();

        Table tableSaved = tableRepository.save(tableToBeSave);

        tableSaved.setOccupied(true);

        Table tableUpdated = tableRepository.save(tableSaved);

        assertThat(tableUpdated).isNotNull();

        assertThat(tableUpdated.isOccupied()).isEqualTo(tableSaved.isOccupied());
    }

    @Test
    @DisplayName("delete remove table when successful")
    void delete_RemoveTable_WhenSuccessful() {
        Table tableToBeSave = TableCreator.createTableToBeSave();

        Table tableSaved = tableRepository.save(tableToBeSave);

        tableRepository.delete(tableSaved);

        Optional<Table> tableFound = tableRepository.findById(tableSaved.getUuid());

        assertThat(tableFound).isEmpty();
    }

    @Test
    @DisplayName("find by number return table when successful")
    void findByNumber_ReturnsTable_WhenSuccessful() {
        Table tableToBeSave = TableCreator.createTableToBeSave();

        Table tableSaved = tableRepository.save(tableToBeSave);

        Optional<Table> tableFound = tableRepository.findByNumber(tableSaved.getNumber());

        assertThat(tableFound).isNotNull().isPresent();

        assertThat(tableFound.get().getNumber()).isEqualTo(tableToBeSave.getNumber());
    }

    @Test
    @DisplayName("save throws ConstraintViolationException when table properties is invalid")
    void save_ThrowsConstraintViolationException_WhenTablePropertiesIsInvalid() {
        Table table = new Table();

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> tableRepository.saveAndFlush(table));

    }

}