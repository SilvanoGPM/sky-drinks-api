package com.github.skyg0d.skydrinksapi.requests;

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
    private String name;

    private String picture;

    @Positive(message = "O valor da bebida deve ser positivo.")
    private double price;

    private boolean alcoholic;

    private String additional;

}
