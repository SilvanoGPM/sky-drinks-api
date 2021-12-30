package com.github.skyg0d.skydrinksapi.exception.details;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class UserRequestsAreLockedExceptionDetails extends ExceptionDetails {

    private String lockedTimestamp;

}
