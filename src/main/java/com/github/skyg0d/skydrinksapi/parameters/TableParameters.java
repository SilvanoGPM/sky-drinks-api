package com.github.skyg0d.skydrinksapi.parameters;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TableParameters {

    @Parameter(description = "Data que a mesa foi criada", example = "2004-04-04T10:16:28.043216", allowEmptyValue = true)
    private String createdAt;

    @Parameter(description = "Data que a mesa foi criada ou depois", example = "2004-04-04", allowEmptyValue = true)
    private String createdInDateOrAfter;

    @Parameter(description = "Data que a mesa foi criada ou antes", example = "2004-04-04", allowEmptyValue = true)
    private String createdInDateOrBefore;

    @Parameter(
            description = "Se o valor for igual a um, pesquisará todas as mesas ocupadas, caso seja zero, pesquisa todas as mesas não ocupadas, e caso seja um menos um, pesquisa todas as mesas",
            example = "-1",
            allowEmptyValue = true
    )
    private int occupied = -1;

    @Parameter(description = "Número de assentos da mesa", example = "8", allowEmptyValue = true)
    private int seats = -1;

    @Parameter(description = "Número de assentos da mesa ou maior que isso", example = "8", allowEmptyValue = true)
    private int greaterThanSeats = -1;

    @Parameter(description = "Número de assentos da mesa ou menor que isso", example = "8", allowEmptyValue = true)
    private int lessThanSeats = -1;

    @Parameter(description = "Número de assentos da mesa, maior que ou igual a isso", example = "8", allowEmptyValue = true)
    private int greaterThanOrEqualToSeats = -1;

    @Parameter(description = "Número de assentos da mesa, menor que ou igual a isso", example = "8", allowEmptyValue = true)
    private int lessThanOrEqualToSeats = -1;

}
