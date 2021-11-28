package com.github.skyg0d.skydrinksapi.enums;

import lombok.Getter;

@Getter
public enum Roles {

    USER("USER", "ROLE_USER"),
    WAITER("WAITER", "ROLE_WAITER"),
    BARMEN("BARMEN", "ROLE_BARMEN"),
    ADMIN("ADMIN", "ROLE_ADMIN");

    private final String name;
    private final String role;

    Roles(String name, String role) {
        this.name = name;
        this.role = role;
    }

    @Override
    public String toString() {
        return role;
    }

}
