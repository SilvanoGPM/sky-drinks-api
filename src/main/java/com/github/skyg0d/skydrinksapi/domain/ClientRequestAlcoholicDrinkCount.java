package com.github.skyg0d.skydrinksapi.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientRequestAlcoholicDrinkCount {

    @Schema(description = "Bebidas alcoólicas ou não", example = "true")
    private boolean alcoholic;

    @Schema(description = "Total de bebidas alcoólicas ou não", example = "53")
    private long total;

}
