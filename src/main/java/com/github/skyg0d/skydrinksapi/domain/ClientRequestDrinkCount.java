package com.github.skyg0d.skydrinksapi.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientRequestDrinkCount {

    @Schema(description = "UUID da bebida")
    private UUID drinkUUID;

    @Schema(description = "Nome da bebida", example = "Blood Mary")
    private String name;

    @Schema(description = "Total de vezes que essa bebida foi pedida", example = "21")
    private long total;

}
