package com.github.skyg0d.skydrinksapi.util.request;

import com.github.skyg0d.skydrinksapi.domain.ClientRequestAlcoholicDrinkCount;

public class ClientRequestAlcoholicDrinkCountCreator {

    public static ClientRequestAlcoholicDrinkCount createClientRequestNotAlcoholicDrinkCount() {
        return ClientRequestAlcoholicDrinkCount
                .builder()
                .alcoholic(false)
                .total(15)
                .build();
    }

    public static ClientRequestAlcoholicDrinkCount createClientRequestAlcoholicDrinkCount() {
        return ClientRequestAlcoholicDrinkCount
                .builder()
                .alcoholic(true)
                .total(10)
                .build();
    }

}
