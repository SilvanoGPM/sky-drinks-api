package com.github.skyg0d.skydrinksapi.mapper;

import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.requests.DrinkPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.DrinkPutRequestBody;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class DrinkMapper {

    public static final DrinkMapper INSTANCE = Mappers.getMapper(DrinkMapper.class);

    public abstract Drink toDrink(DrinkPostRequestBody drinkPostRequestBody);

    public abstract Drink toDrink(DrinkPutRequestBody drinkPutRequestBody);

    UUID mapUuid(String uuid) {
        return UUID.fromString(uuid);
    }

}
