package com.github.skyg0d.skydrinksapi.util.table;

import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.domain.Table;
import com.github.skyg0d.skydrinksapi.enums.ClientRequestStatus;
import com.github.skyg0d.skydrinksapi.util.drink.DrinkCreator;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TableCreator {

    public static final UUID uuid = UUID.fromString("dd6cc769-326c-4e14-be42-57d49ebeb315");

    public static Table createTableToBeSave() {
        return Table
                .builder()
                .number(1)
                .seats(4)
                .occupied(false)
                .requests(createClientRequest())
                .build();
    }

    public static Table createValidTable() {
        return Table
                .builder()
                .uuid(uuid)
                .number(1)
                .seats(4)
                .occupied(false)
                .requests(createClientRequest())
                .build();
    }

    public static Table createValidUpdatedTable() {
        return Table
                .builder()
                .uuid(uuid)
                .number(1)
                .seats(4)
                .occupied(true)
                .requests(createClientRequest())
                .build();
    }

    public static Table createValidSwitchedTable() {
        return Table
                .builder()
                .uuid(uuid)
                .number(1)
                .seats(4)
                .occupied(true)
                .requests(createClientRequest())
                .build();
    }

    private static Set<ClientRequest> createClientRequest() {
        Table table = Table
                .builder()
                .uuid(uuid)
                .number(1)
                .seats(4)
                .occupied(true)
                .build();

        return Set.of(ClientRequest
                .builder()
                .user(ApplicationUserCreator.createValidApplicationUser())
                .drinks(new ArrayList<>(List.of(DrinkCreator.createValidDrink())))
                .totalPrice(DrinkCreator.createValidDrink().getPrice())
                .status(ClientRequestStatus.PROCESSING)
                .table(table)
                .build());
    }

}
