package com.github.skyg0d.skydrinksapi.util.password;

import com.github.skyg0d.skydrinksapi.requests.RequestPasswordReset;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserCreator;

public class RequestPasswordResetCreator {

    public static RequestPasswordReset createRequestPasswordReset() {
        return RequestPasswordReset
                .builder()
                .email(ApplicationUserCreator.createValidApplicationUser().getEmail())
                .build();
    }

}
