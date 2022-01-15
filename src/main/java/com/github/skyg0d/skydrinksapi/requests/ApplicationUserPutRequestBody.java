package com.github.skyg0d.skydrinksapi.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Identificador único do usuário", example = "7df52bef-31b0-4dc4-bdf3-ea757b5ff8ab")
    private UUID uuid;

    @Size(min = 3, max = 250, message = "O nome do usuário deve ter entre 3 e 250 caracteres.")
    @NotBlank(message = "O nome do usuário não pode ficar vazio.")
    @Schema(description = "Nome do usuário", example = "Roger")
    private String name;

    @NotBlank(message = "O email do usuário não pode ficar vazio.")
    @Email(message = "O email não é válido.")
    @Schema(description = "Email do usuário", example = "roger@mail.com")
    private String email;

    @NotBlank(message = "A senha do usuário não pode ficar vazia.")
    @Size(min = 8, message = "A senha precisa ter pelo menos 8 caracteres")
    @Schema(description = "Senha do usuário", example = "roger123")
    private String password;

    @NotBlank(message = "A função do usuário não pode ficar vazia.")
    @Schema(description = "Função do usuário", example = "BARMEN")
    private String role = "USER";

    @NotNull(message = "A data de nascimento do usuário não pode ficar vazia.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Data de nascimento do usuário", example = "2004-04-09")
    private LocalDate birthDay;

    @NotNull(message = "O CPF do usuário não pode ficar vazio.")
    @CPF(message = "O CPF do usuário não é valido!")
    @Schema(description = "CPF do usuário", example = "123.456.789-10")
    private String cpf;

}
