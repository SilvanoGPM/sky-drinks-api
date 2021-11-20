package com.github.skyg0d.skydrinksapi.repository;

import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.repository.drink.DrinkRepository;
import com.github.skyg0d.skydrinksapi.util.drink.DrinkCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@DisplayName("Tests for Drink Repository")
class DrinkRepositoryTest {

    @Autowired
    private DrinkRepository drinkRepository;

    @Test
    @DisplayName("save persists drink when successful")
    void save_PersistDrink_WhenSuccessful() {
        Drink drinkToBeSave = DrinkCreator.createDrinkToBeSave();

        Drink drinkSaved = drinkRepository.save(drinkToBeSave);

        assertThat(drinkSaved).isNotNull();

        assertThat(drinkSaved.getName())
                .isNotNull()
                .isEqualTo(drinkSaved.getName());
    }

    @Test
    @DisplayName("save updates drink when successful")
    void save_UpdateDrink_WhenSuccessful() {
        Drink drinkToBeSave = DrinkCreator.createDrinkToBeSave();

        Drink drinkSaved = drinkRepository.save(drinkToBeSave);

        drinkSaved.setName("Suco de limão com laranja");

        Drink drinkUpdated = drinkRepository.save(drinkSaved);

        assertThat(drinkUpdated).isNotNull();

        assertThat(drinkUpdated.getName())
                .isNotNull()
                .isEqualTo(drinkUpdated.getName());
    }

    @Test
    @DisplayName("delete remove drink when successful")
    void delete_RemoveDrink_WhenSuccessful() {
        Drink drinkToBeSave = DrinkCreator.createDrinkToBeSave();

        Drink drinkSaved = drinkRepository.save(drinkToBeSave);

        drinkRepository.delete(drinkSaved);

        Optional<Drink> drinkFound = drinkRepository.findById(drinkSaved.getUuid());

        assertThat(drinkFound).isEmpty();
    }

    @Test
    @DisplayName("save throws ConstraintViolationException when drink properties is invalid")
    void save_ThrowsConstraintViolationException_WhenDrinkPropertiesIsInvalid() {
        Drink drink = new Drink();

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> drinkRepository.saveAndFlush(drink));

    }

}