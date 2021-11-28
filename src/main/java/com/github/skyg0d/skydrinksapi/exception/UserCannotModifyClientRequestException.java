package com.github.skyg0d.skydrinksapi.exception;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class UserCannotModifyClientRequestException extends RuntimeException {

    private final ApplicationUser triedUser;
    private final ClientRequest request;

    public UserCannotModifyClientRequestException(String message, ApplicationUser triedUser, ClientRequest request) {
        super(message);
        this.triedUser = triedUser;
        this.request = request;
    }

}
