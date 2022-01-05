package com.github.skyg0d.skydrinksapi.util.request;

import com.github.skyg0d.skydrinksapi.domain.ClientRequestDrinkCount;
import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.util.drink.DrinkCreator;

public class ClientRequestDrinkCountCreator {

    public static ClientRequestDrinkCount createClientRequestDrinkCount() {
        Drink drink = DrinkCreator.createValidDrink();

        return ClientRequestDrinkCount
                .builder()
                .drinkUUID(drink.getUuid())
                .name(drink.getName())
                .total(10)
                .build();
    }

}
