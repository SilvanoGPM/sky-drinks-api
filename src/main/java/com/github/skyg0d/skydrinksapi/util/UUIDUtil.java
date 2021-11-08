package com.github.skyg0d.skydrinksapi.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDUtil {

    public UUID getUUID(String uuid) {
        try{
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException exception){
            return null;
        }
    }

}
