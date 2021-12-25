package com.github.skyg0d.skydrinksapi.util.request;

import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPutRequestBody;

public class ClientRequestPutRequestBodyCreator {

    public static ClientRequestPutRequestBody createClientRequestPutRequestBodyCreatorToBeUpdate() {
        ClientRequest request = ClientRequestCreator.createValidUpdatedClientRequest();

        return ClientRequestPutRequestBody
                .builder()
                .uuid(request.getUuid())
                .drinks(request.getDrinks())
                .table(request.getTable())
                .build();
    }

}
