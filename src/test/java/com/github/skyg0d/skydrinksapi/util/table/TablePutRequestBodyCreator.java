package com.github.skyg0d.skydrinksapi.util.table;

import com.github.skyg0d.skydrinksapi.domain.Table;
import com.github.skyg0d.skydrinksapi.requests.TablePutRequestBody;

public class TablePutRequestBodyCreator {

    public static TablePutRequestBody createTablePutRequestBodyToUpdate() {
        Table table = TableCreator.createValidUpdatedTable();

        return TablePutRequestBody
                .builder()
                .uuid(table.getUuid())
                .number(table.getNumber())
                .seats(table.getSeats())
                .build();
    }

}
