package com.github.skyg0d.skydrinksapi.exception.details;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class EmailAlreadyExistsExceptionDetails extends ExceptionDetails {

    private String email;

}
