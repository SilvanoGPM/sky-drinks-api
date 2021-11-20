package com.github.skyg0d.skydrinksapi.util.table;

import com.github.skyg0d.skydrinksapi.domain.Table;

import java.util.UUID;

public class TableCreator {

    public static final UUID uuid = UUID.fromString("dd6cc769-326c-4e14-be42-57d49ebeb315");

    public static Table createTableToBeSave() {
        return Table
                .builder()
                .number(1)
                .seats(4)
                .occupied(false)
                .build();
    }

    public static Table createValidTable() {
        return Table
                .builder()
                .uuid(uuid)
                .number(1)
                .seats(4)
                .occupied(false)
                .build();
    }

    public static Table createValidUpdatedTable() {
        return Table
                .builder()
                .uuid(uuid)
                .number(1)
                .seats(4)
                .occupied(true)
                .build();
    }

}
