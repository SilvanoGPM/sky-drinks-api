package com.github.skyg0d.skydrinksapi.mapper;

import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPutRequestBody;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class ClientRequestMapper {

    public static final ClientRequestMapper INSTANCE = Mappers.getMapper(ClientRequestMapper.class);

    public abstract ClientRequest toClientRequest(ClientRequestPostRequestBody clientRequestPostRequestBody);

    public abstract ClientRequest toClientRequest(ClientRequestPutRequestBody clientRequestPutRequestBody);

}
