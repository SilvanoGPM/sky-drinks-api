package com.github.skyg0d.skydrinksapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class UserCannotCompleteClientRequestException extends RuntimeException {

    private final String reason;

    public UserCannotCompleteClientRequestException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

}
