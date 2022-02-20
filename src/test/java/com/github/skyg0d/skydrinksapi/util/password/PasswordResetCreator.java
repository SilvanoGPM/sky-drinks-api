package com.github.skyg0d.skydrinksapi.util.password;

import com.github.skyg0d.skydrinksapi.domain.PasswordReset;

import java.time.LocalDateTime;
import java.util.UUID;

public class PasswordResetCreator {

    public static final UUID uuid = UUID.fromString("2d02afb9-2a7e-4565-9602-495bc5f6fd46");

    public static PasswordReset createPasswordResetToBeSave() {
        return PasswordReset
                .builder()
                .uuid(uuid)
                .token("ABCDEFGHIJ")
                .expireDate(LocalDateTime.now())
                .build();
    }

    public static PasswordReset createValidPasswordReset() {
        return PasswordReset
                .builder()
                .uuid(uuid)
                .token("ABCDEFGHIJ")
                .expireDate(LocalDateTime.now())
                .build();
    }

    public static PasswordReset createValidUpdatedPasswordReset() {
        return PasswordReset
                .builder()
                .uuid(uuid)
                .token("ABCDEFGHIJ")
                .expireDate(LocalDateTime.now())
                .resetFinished(true)
                .build();
    }

}
