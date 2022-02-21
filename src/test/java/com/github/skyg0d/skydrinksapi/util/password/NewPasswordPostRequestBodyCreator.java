package com.github.skyg0d.skydrinksapi.util.password;

import com.github.skyg0d.skydrinksapi.domain.PasswordReset;
import com.github.skyg0d.skydrinksapi.requests.NewPasswordPostRequestBody;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserCreator;

import java.time.LocalDateTime;
import java.util.UUID;

public class NewPasswordPostRequestBodyCreator {

    public static NewPasswordPostRequestBody createPasswordResetToBeSave() {
        PasswordReset passwordReset = PasswordResetCreator.createPasswordResetToBeSave();

        return NewPasswordPostRequestBody
                .builder()
                .token(passwordReset.getToken())
                .email(ApplicationUserCreator.createValidApplicationUser().getEmail())
                .password("newpassword")
                .build();
    }

}
