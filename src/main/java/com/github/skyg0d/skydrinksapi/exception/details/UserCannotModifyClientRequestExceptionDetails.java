package com.github.skyg0d.skydrinksapi.exception.details;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class UserCannotModifyClientRequestExceptionDetails extends ExceptionDetails {

    private final ApplicationUser triedUser;
    private final ClientRequest request;

}
