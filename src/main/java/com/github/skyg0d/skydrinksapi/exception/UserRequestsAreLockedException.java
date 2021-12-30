package com.github.skyg0d.skydrinksapi.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserRequestsAreLockedException extends BadRequestException {

    private final String lockedTimestamp;

    public UserRequestsAreLockedException(String message, LocalDateTime lockedTimestamp) {
        super(message);
        this.lockedTimestamp = lockedTimestamp.toString();
    }

}
