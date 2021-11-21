package com.github.skyg0d.skydrinksapi.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Tests for UUID Util")
class UUIDUtilTest {

    private final UUIDUtil uuidUtil = new UUIDUtil();

    @Test
    @DisplayName("getUUID returns an UUID when successful")
    void getUUID_ReturnsAnUUID_WhenSuccessful() {
                UUID uuid = uuidUtil.getUUID(UUID.randomUUID().toString());

        assertThat(uuid).isNotNull();
    }

    @Test
    @DisplayName("getUUID returns null when string is not an UUID")
    void getUUID_ReturnsNull_WhenStringIsNotAnUUID() {
        UUID uuid = uuidUtil.getUUID("");

        assertThat(uuid).isNull();
    }

}