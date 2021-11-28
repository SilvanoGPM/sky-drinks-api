package com.github.skyg0d.skydrinksapi.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationUserPostRequestBody {

    @Size(min = 3, max = 250, message = "O nome do usuário deve ter entre 3 e 250 caracteres.")
    @NotBlank(message = "O nome do usuário não pode ficar vazio.")
    private String name;

    @NotBlank(message = "O email do usuário não pode ficar vazio.")
    @Email(message = "O email não é válido.")
    private String email;

    @NotBlank(message = "A senha do usuário não pode ficar vazia.")
    private String password;

    @NotBlank(message = "A função do usuário não pode ficar vazia.")
    private String role = "USER";

}
