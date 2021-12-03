package com.github.skyg0d.skydrinksapi.exception.details;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class UserUniqueFieldExistsDetails extends ExceptionDetails {

    @Schema(description = "Campo único que já existe", example = "Email: mail@mail.com")
    private String unique;

}
