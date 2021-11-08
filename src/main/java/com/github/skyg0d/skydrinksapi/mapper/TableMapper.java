package com.github.skyg0d.skydrinksapi.mapper;

import com.github.skyg0d.skydrinksapi.domain.Table;
import com.github.skyg0d.skydrinksapi.requests.TablePostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.TablePutRequestBody;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public abstract class TableMapper {

    public static final TableMapper INSTANCE = Mappers.getMapper(TableMapper.class);

    public abstract Table toTable(TablePostRequestBody tablePostRequestBody);

    public abstract Table toTable(TablePutRequestBody tablePutRequestBody);

}
