package com.github.skyg0d.skydrinksapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class UserUniqueFieldExistsException extends RuntimeException {

    private final String unique;

    public UserUniqueFieldExistsException(String message, String unique) {
        super(message);
        this.unique = unique;
    }

}
