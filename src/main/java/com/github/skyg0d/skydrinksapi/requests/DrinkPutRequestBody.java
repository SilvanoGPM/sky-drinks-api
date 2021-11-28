package com.github.skyg0d.skydrinksapi.requests;

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
    private UUID uuid;

    @Size(min = 3, max = 100, message = "O nome da bebida precisa ter de 3 a 100 caracteres.")
    @NotBlank(message = "O nome da bebida não pode ser vazio.")
    private String name;

    private String picture;

    private String description;

    @Positive(message = "O valor da bebida deve ser positivo.")
    private double price;

    private boolean alcoholic;

    private String additional;

}
