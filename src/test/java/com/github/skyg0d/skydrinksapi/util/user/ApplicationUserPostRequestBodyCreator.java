package com.github.skyg0d.skydrinksapi.util.user;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.requests.ApplicationUserPostRequestBody;

public class ApplicationUserPostRequestBodyCreator {

    public static ApplicationUserPostRequestBody createApplicationUserPostRequestBodyToBeSave() {
        ApplicationUser applicationUser = ApplicationUserCreator.createApplicationUserToBeSave();

        return ApplicationUserPostRequestBody
                .builder()
                .name(applicationUser.getName())
                .role(applicationUser.getRole())
                .cpf(applicationUser.getCpf())
                .email(applicationUser.getEmail())
                .password(applicationUser.getPassword())
                .birthDay(applicationUser.getBirthDay())
                .build();
    }

}
