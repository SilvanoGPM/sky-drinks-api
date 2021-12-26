package com.github.skyg0d.skydrinksapi.exception.details;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCannotCompleteClientRequestExceptionDetails extends ExceptionDetails {

    @Schema(description = "Motivo pelo qual o usuário não pode completar o pedido", example = "Usuário não possuí permissões suficientes")
    private String reason;

}
