package com.github.skyg0d.skydrinksapi.util.password;

import com.github.skyg0d.skydrinksapi.domain.PasswordReset;
import com.github.skyg0d.skydrinksapi.requests.VerifyTokenPostRequestBody;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserCreator;

public class VerifyTokenPostRequestBodyCretor {

    public static VerifyTokenPostRequestBody createVerifyTokenPostRequestBody() {
        PasswordReset passwordReset = PasswordResetCreator.createValidPasswordReset();

        return VerifyTokenPostRequestBody
                .builder()
                .token(passwordReset.getToken())
                .email(ApplicationUserCreator.createValidApplicationUser().getEmail())
                .build();
    }

}
