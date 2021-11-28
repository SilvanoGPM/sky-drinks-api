package com.github.skyg0d.skydrinksapi.util.user;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.requests.ApplicationUserPutRequestBody;

public class ApplicationUserPutRequestBodyCreator {

    public static ApplicationUserPutRequestBody createApplicationUserPutRequestBodyToBeSave() {
        ApplicationUser applicationUser = ApplicationUserCreator.createUpdatedApplicationUser();

        return ApplicationUserPutRequestBody
                .builder()
                .uuid(applicationUser.getUuid())
                .name(applicationUser.getName())
                .role(applicationUser.getRole())
                .cpf(applicationUser.getCpf())
                .email(applicationUser.getEmail())
                .password(applicationUser.getPassword())
                .birthDay(applicationUser.getBirthDay())
                .build();
    }

}
