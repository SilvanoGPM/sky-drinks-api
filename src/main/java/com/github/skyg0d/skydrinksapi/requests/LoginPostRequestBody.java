package com.github.skyg0d.skydrinksapi.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginPostRequestBody {

    @NotBlank(message = "O email do usuário não pode ficar vazio.")
    @Email(message = "O email não é válido.")
    @Schema(description = "Email do usuário", example = "roger@mail.com")
    private String email;

    @NotBlank(message = "A senha do usuário não pode ficar vazia.")
    @Schema(description = "Senha do usuário", example = "roger123")
    private String password;

}
