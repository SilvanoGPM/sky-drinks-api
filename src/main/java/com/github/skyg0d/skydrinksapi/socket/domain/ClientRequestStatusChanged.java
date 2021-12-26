package com.github.skyg0d.skydrinksapi.socket.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ClientRequestStatusChanged {

    private String message;
    private UUID uuid;

}
