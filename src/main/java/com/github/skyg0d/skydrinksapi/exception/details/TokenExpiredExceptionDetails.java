package com.github.skyg0d.skydrinksapi.exception.details;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenExpiredExceptionDetails extends ExceptionDetails {

    private boolean expired;

}
