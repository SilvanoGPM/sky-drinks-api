package com.github.skyg0d.skydrinksapi.util.request;

import com.github.skyg0d.skydrinksapi.domain.ClientRequestDate;
import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.util.drink.DrinkCreator;

public class ClientRequestDateCreator {

    public static ClientRequestDate createClientRequestDate() {
        Drink drink = DrinkCreator.createValidDrink();

        return ClientRequestDate
                .builder()
                .date(drink.getCreatedAt().toLocalDate())
                .build();
    }

}
