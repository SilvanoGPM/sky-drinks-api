package com.github.skyg0d.skydrinksapi.util.user;

import com.github.skyg0d.skydrinksapi.requests.LoginPostRequestBody;

public class LoginPostRequestBodyCreator {

    public static LoginPostRequestBody createLoginPostRequestBody() {
        return LoginPostRequestBody
                .builder()
                .email("admin@mail.com")
                .password("admin123")
                .build();
    }

}
