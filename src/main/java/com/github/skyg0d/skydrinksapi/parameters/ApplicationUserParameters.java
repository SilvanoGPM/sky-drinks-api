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
public class ApplicationUserParameters {

    @Parameter(description = "Nome do usuário para pesquisa", example = "Roger", allowEmptyValue = true)
    private String name;

    @Parameter(description = "Função do usuário para pesquisa", example = "BARMEN", allowEmptyValue = true)
    private String role;

    @Parameter(description = "Data que o usuário foi criado", example = "2004-04-04", allowEmptyValue = true)
    private String createdAt;

    @Parameter(description = "Data que o usuário foi criado ou depois", example = "2004-04-04", allowEmptyValue = true)
    private String createdInDateOrAfter;

    @Parameter(description = "Data que o usuário foi criado ou antes", example = "2004-04-04", allowEmptyValue = true)
    private String createdInDateOrBefore;

    @Parameter(description = "Data de nascimento do usuário", example = "2004-04-04", allowEmptyValue = true)
    private String birthDay;

    @Parameter(description = "Data de nascimento do usuário ou depois", example = "2004-04-04", allowEmptyValue = true)
    private String birthInDateOrAfter;

    @Parameter(description = "Data de nascimento do usuário ou antes", example = "2004-04-04", allowEmptyValue = true)
    private String birthInDateOrBefore;

}
