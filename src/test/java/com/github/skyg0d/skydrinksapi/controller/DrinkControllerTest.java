package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.parameters.DrinkParameters;
import com.github.skyg0d.skydrinksapi.requests.DrinkPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.DrinkPutRequestBody;
import com.github.skyg0d.skydrinksapi.service.DrinkService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for DrinkController")
class DrinkControllerTest {

    @InjectMocks
    private DrinkController drinkController;

    @Mock
    private DrinkService drinkServiceMock;

    @BeforeEach
    void setUp() {
        Page<Drink> drinkPage = new PageImpl<>(List.of(DrinkCreator.createValidDrink()));

        BDDMockito
                .when(drinkServiceMock.listAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(drinkPage);

        BDDMockito
                .when(drinkServiceMock.findByIdOrElseThrowBadRequestException(ArgumentMatchers.any(UUID.class)))
                .thenReturn(DrinkCreator.createValidDrink());

        BDDMockito
                .when(drinkServiceMock.search(ArgumentMatchers.any(DrinkParameters.class), ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(drinkPage);

        BDDMockito
                .when(drinkServiceMock.save(ArgumentMatchers.any(DrinkPostRequestBody.class)))
                .thenReturn(DrinkCreator.createValidDrink());

        BDDMockito
                .doNothing()
                .when(drinkServiceMock)
                .replace(ArgumentMatchers.any(DrinkPutRequestBody.class));

        BDDMockito
                .doNothing()
                .when(drinkServiceMock)
                .delete(ArgumentMatchers.any(UUID.class));
    }

    @Test
    @DisplayName("listAll return list of drinks inside page object when successful")
    void listAll_ReturnListOfDrinksInsidePageObject_WhenSuccessful() {
        Drink expectedDrink = DrinkCreator.createValidDrink();

        ResponseEntity<Page<Drink>> entity = drinkController.listAll(PageRequest.of(1, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().toList())
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedDrink);
    }

    @Test
    @DisplayName("listAll return empty page when there are no drinks")
    void listAll_ReturnListOfDrinksInsidePageObject_WhenThereAreNoDrinks() {
        BDDMockito
                .when(drinkServiceMock.listAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(Page.empty());

        ResponseEntity<Page<Drink>> entity = drinkController.listAll(PageRequest.of(1, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isEmpty();
    }

    @Test
    @DisplayName("findById return list of drinks inside page object when successful")
    void findById_ReturnListOfDrinksInsidePageObject_WhenSuccessful() {
        Drink expectedDrink = DrinkCreator.createValidDrink();

        ResponseEntity<Drink> entity = drinkController.findById(UUID.randomUUID());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedDrink);
    }

    @Test
    @DisplayName("search return list of drinks inside page object when successful")
    void search_ReturnListOfDrinksInsidePageObject_WhenSuccessful() {
        Drink expectedDrink = DrinkCreator.createValidDrink();

        ResponseEntity<Page<Drink>> entity = drinkController.search(new DrinkParameters(), PageRequest.of(1, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().toList())
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedDrink);
    }

    @Test
    @DisplayName("save creates drink when successful")
    void save_CreatesDrink_WhenSuccessful() {
        Drink expectedDrink = DrinkCreator.createValidDrink();

        ResponseEntity<Drink> entity = drinkController.save(DrinkPostRequestBodyCreator.createDrinkPostRequestBodyToBeSave());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody())
                .isNotNull()
                .isEqualTo(expectedDrink);
    }

    @Test
    @DisplayName("replace updates drink when successful")
    void replace_UpdatedDrink_WhenSuccessful() {
        ResponseEntity<Void> entity = drinkController.replace(DrinkPutRequestBodyCreator.createDrinkPutRequestBodyToBeUpdate());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete removes drink when successful")
    void delete_RemovesDrink_WhenSuccessful() {
        ResponseEntity<Void> entity = drinkController.delete(UUID.randomUUID());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

}