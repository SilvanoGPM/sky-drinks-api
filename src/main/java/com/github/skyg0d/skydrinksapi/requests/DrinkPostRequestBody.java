package com.github.skyg0d.skydrinksapi.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrinkPostRequestBody {

    @Size(min = 3, max = 100, message = "O nome da bebida precisa ter de 3 a 100 caracteres.")
    @NotBlank(message = "O nome da bebida não pode ficar vazio.")
    @Schema(description = "Nome do drink", example = "Blood Mary")
    private String name;

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
