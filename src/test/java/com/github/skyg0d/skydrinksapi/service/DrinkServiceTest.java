package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.exception.BadRequestException;
import com.github.skyg0d.skydrinksapi.parameters.DrinkParameters;
import com.github.skyg0d.skydrinksapi.repository.drink.DrinkRepository;
import com.github.skyg0d.skydrinksapi.util.drink.DrinkCreator;
import com.github.skyg0d.skydrinksapi.util.drink.DrinkPostRequestBodyCreator;
import com.github.skyg0d.skydrinksapi.util.drink.DrinkPutRequestBodyCreator;
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
class DrinkServiceTest {

    @InjectMocks
    private DrinkService drinkService;

    @Mock
    private DrinkRepository drinkRepository;

    @BeforeEach
    void setUp() {
        Page<Drink> drinkPage = new PageImpl<>(List.of(DrinkCreator.createValidDrink()));

        BDDMockito
                .when(drinkRepository.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(drinkPage);

        BDDMockito
                .when(drinkRepository.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(DrinkCreator.createValidDrink()));

        BDDMockito
                .when(drinkRepository.findAll(ArgumentMatchers.<Specification<Drink>>any(), ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(drinkPage);

        BDDMockito
                .when(drinkRepository.save(ArgumentMatchers.any(Drink.class)))
                .thenReturn(DrinkCreator.createValidDrink());

        BDDMockito
                .doNothing()
                .when(drinkRepository)
                .delete(ArgumentMatchers.any(Drink.class));
    }

    @Test
    @DisplayName("listAll return list of drinks inside page object when successful")
    void listAll_ReturnListOfDrinksInsidePageObject_WhenSuccessful() {
        Drink expectedDrink = DrinkCreator.createValidDrink();

        Page<Drink> drinkPage = drinkService.listAll(PageRequest.of(1, 1));

        assertThat(drinkPage).isNotNull();

        assertThat(drinkPage.toList())
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedDrink);
    }

    @Test
    @DisplayName("listAll return empty page when there are no drinks")
    void listAll_ReturnListOfDrinksInsidePageObject_WhenThereAreNoDrinks() {
        BDDMockito
                .when(drinkRepository.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(Page.empty());

        Page<Drink> drinkPage = drinkService.listAll(PageRequest.of(1, 1));

        assertThat(drinkPage).isEmpty();
    }

    @Test
    @DisplayName("findByIdOrElseThrowBadRequestException return list of drinks inside page object when successful")
    void findByIdOrElseThrowBadRequestException_ReturnListOfDrinksInsidePageObject_WhenSuccessful() {
        Drink expectedDrink = DrinkCreator.createValidDrink();

        Drink drinkFound = drinkService.findByIdOrElseThrowBadRequestException(UUID.randomUUID());

        assertThat(drinkFound)
                .isNotNull()
                .isEqualTo(expectedDrink);
    }

    @Test
    @DisplayName("search return list of drinks inside page object when successful")
    void search_ReturnListOfDrinksInsidePageObject_WhenSuccessful() {
        Drink expectedDrink = DrinkCreator.createValidDrink();

        Page<Drink> drinkPage = drinkService.search(new DrinkParameters(), PageRequest.of(1, 1));

        assertThat(drinkPage).isNotNull();

        assertThat(drinkPage.toList())
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedDrink);
    }

    @Test
    @DisplayName("save creates drink when successful")
    void save_CreatesDrink_WhenSuccessful() {
        Drink expectedDrink = DrinkCreator.createValidDrink();

        Drink drinkSaved = drinkService.save(DrinkPostRequestBodyCreator.createDrinkPostRequestBodyToBeSave());

        assertThat(drinkSaved)
                .isNotNull()
                .isEqualTo(expectedDrink);
    }

    @Test
    @DisplayName("replace updates drink when successful")
    void replace_UpdatedDrink_WhenSuccessful() {
        BDDMockito
                .when(drinkRepository.save(ArgumentMatchers.any(Drink.class)))
                .thenReturn(DrinkCreator.createValidUpdatedDrink());


        assertThatCode(() -> drinkService.replace(DrinkPutRequestBodyCreator.createDrinkPutRequestBodyToBeUpdate()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("replace removes drink when successful")
    void delete_RemovesDrink_WhenSuccessful() {
        assertThatCode(() -> drinkService.delete(UUID.randomUUID()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("findByIdOrElseThrowBadRequestException throws BadRequestException when drink is not found")
    void findByIdOrElseThrowBadRequestException_ThrowsBadRequestException_WhenDrinkIsNotFound() {
        BDDMockito
                .when(drinkRepository.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> drinkService.findByIdOrElseThrowBadRequestException(UUID.randomUUID()));
    }

}