package com.github.skyg0d.skydrinksapi.util.drink;

import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.requests.DrinkPutRequestBody;

public class DrinkPutRequestBodyCreator {

    public static DrinkPutRequestBody createDrinkPutRequestBodyToBeUpdate() {
        Drink drink = DrinkCreator.createValidUpdatedDrink();

        return DrinkPutRequestBody
                .builder()
                .uuid(drink.getUuid())
                .name(drink.getName())
                .volume(drink.getVolume())
                .description(drink.getDescription())
                .additional(String.join(Drink.ADDITIONAL_SEPARATOR, drink.getAdditionalList()))
                .alcoholic(drink.isAlcoholic())
                .price(drink.getPrice())
                .picture(drink.getPicture())
                .build();
    }

}
