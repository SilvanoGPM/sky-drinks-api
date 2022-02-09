package com.github.skyg0d.skydrinksapi.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewPasswordPostRequestBody {

    @NotBlank(message = "O email do usuário não pode ficar vazio.")
    @Email(message = "O email não é válido.")
    @Schema(description = "Email do usuário", example = "roger@mail.com")
    private String email;

    @NotBlank(message = "A nova senha não pode ficar vazia.")
    @Size(min = 8, message = "A nova senha precisa ter pelo menos 8 caracteres")
    @Schema(description = "Nova senha do usuário", example = "roger123")
    private String password;

    @NotBlank(message = "O código de confirmação não pode ficar vazio.")
    @Schema(description = "Código de confirmação para restaurar senha", example = "qfq71VC13")
    private String token;

}
