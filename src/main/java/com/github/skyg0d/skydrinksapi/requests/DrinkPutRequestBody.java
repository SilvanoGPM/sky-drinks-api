package com.github.skyg0d.skydrinksapi.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrinkPutRequestBody {

    @NotNull(message = "UUID do drink não pode ficar vazio.")
    @Schema(description = "UUID do drink", example = "313fd316-ca17-429c-b7cf-5b96f4d74595")
    private UUID uuid;

    @Size(min = 3, max = 100, message = "O nome da bebida precisa ter de 3 a 100 caracteres.")
    @NotBlank(message = "O nome da bebida não pode ser vazio.")
    @Schema(description = "Nome do drink", example = "Blood Mary")
    private String name;

    @Positive(message = "O volume da bebida deve ser positivo.")
    @Schema(description = "O volume da bebida em mililitros.", example = "1000")
    private int volume;

    @Schema(description = "Imagem do drink", example = "blood_mary.png")
    private String picture;

    @Schema(description = "Descrição do drink", example = "Drink Refrescante")
    private String description;

    @Positive(message = "O valor da bebida deve ser positivo.")
    @Schema(description = "Preço do drink", example = "10.25")
    private double price;

    @Schema(description = "Drink alcoólico", example = "false")
    private boolean alcoholic;

    @Schema(description = "Adicionais do drink", example = "gelo;limão")
    private String additional;

}
