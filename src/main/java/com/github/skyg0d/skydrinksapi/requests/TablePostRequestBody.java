package com.github.skyg0d.skydrinksapi.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TablePostRequestBody {

    @Range(min = 1, max = 100, message = "O número de assentos de uma mesa deve estar entre 1 e 100.")
    @Positive(message = "O número de assentos em uma mesa deve ser positivo.")
    private int seats;

    @Positive(message = "O número da mesa deve ser positivo.")
    private int number;

    private boolean occupied;

}
