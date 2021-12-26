package com.github.skyg0d.skydrinksapi.exception.details;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class UserCannotModifyClientRequestExceptionDetails extends ExceptionDetails {

    private final ApplicationUser triedUser;
    private final ClientRequest request;

}
