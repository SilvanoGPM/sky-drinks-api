package com.github.skyg0d.skydrinksapi.util.table;

import com.github.skyg0d.skydrinksapi.domain.Table;
import com.github.skyg0d.skydrinksapi.requests.TablePostRequestBody;

public class TablePostRequestBodyCreator {

    public static TablePostRequestBody createTablePostRequestBodyToBeSave() {
        Table table = TableCreator.createTableToBeSave();

        return TablePostRequestBody
                .builder()
                .number(table.getNumber())
                .occupied(table.isOccupied())
                .seats(table.getSeats())
                .build();
    }

}
