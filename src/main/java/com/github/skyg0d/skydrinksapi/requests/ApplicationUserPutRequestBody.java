package com.github.skyg0d.skydrinksapi.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationUserPutRequestBody {

    @NotNull(message = "UUID do usuário não pode ficar vazio.")
    private UUID uuid;

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

    @NotNull(message = "A data de nascimento do usuário não pode ficar vazia.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDay;

    @NotNull(message = "O CPF do usuário não pode ficar vazio.")
    @CPF(message = "O CPF do usuário não é valido!")
    private String cpf;

}
