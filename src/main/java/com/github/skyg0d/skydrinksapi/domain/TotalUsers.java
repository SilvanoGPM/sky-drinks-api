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
public class TotalUsers {

    @Schema(description = "Total de usuários", example = "33")
    public long total;

    @Schema(description = "Total de usuários bloqueados", example = "12")
    public long locked;

    @Schema(description = "Total de usuários desbloquados", example = "21")
    public long unlocked;

}
