package com.github.skyg0d.skydrinksapi.util.request;

import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.util.drink.DrinkCreator;
import com.github.skyg0d.skydrinksapi.util.table.TableCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientRequestCreator {

    private static final UUID uuid = UUID.fromString("da278fd9-cc0a-464b-97ad-9a5215fb8fcf");

    public static ClientRequest createClientRequestToBeSave() {
        return ClientRequest
                .builder()
                .table(TableCreator.createValidTable())
                .drinks(new ArrayList<>(List.of(DrinkCreator.createValidDrink())))
                .totalPrice(0.0)
                .finished(false)
                .build();
    }

    public static ClientRequest createValidClientRequest() {
        return ClientRequest
                .builder()
                .uuid(uuid)
                .drinks(new ArrayList<>(List.of(DrinkCreator.createValidDrink())))
                .table(TableCreator.createValidTable())
                .totalPrice(0.0)
                .finished(false)
                .build();
    }

    public static ClientRequest createValidUpdatedClientRequest() {
        return ClientRequest
                .builder()
                .uuid(uuid)
                .drinks(new ArrayList<>(List.of(DrinkCreator.createValidDrink())))
                .table(TableCreator.createValidTable())
                .totalPrice(DrinkCreator.createValidDrink().getPrice())
                .finished(true)
                .build();
    }

}
