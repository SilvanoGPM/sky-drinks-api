package com.github.skyg0d.skydrinksapi.util.request;

import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPutRequestBody;

public class ClientRequestPostRequestBodyCreator {

    public static ClientRequestPostRequestBody createClientRequestPostRequestBodyToBeSave() {
        ClientRequest request = ClientRequestCreator.createClientRequestToBeSave();

        return ClientRequestPostRequestBody
                .builder()
                .drinks(request.getDrinks())
                .table(request.getTable())
                .build();
    }

}
