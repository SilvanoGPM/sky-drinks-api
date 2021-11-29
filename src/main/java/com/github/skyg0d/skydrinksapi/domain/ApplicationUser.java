package com.github.skyg0d.skydrinksapi.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "application_users")
@Entity
public class ApplicationUser extends BaseEntity {

    @Size(min = 3, max = 250, message = "O nome do usuário deve ter entre 3 e 250 caracteres.")
    @NotBlank(message = "O nome do usuário não pode ficar vazio.")
    private String name;

    @NotBlank(message = "O email do usuário não pode ficar vazio.")
    @Email(message = "O email não é válido.")
    private String email;

    @NotBlank(message = "A senha do usuário não pode ficar vazia.")
    @ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "A função do usuário não pode ficar vazia.")
    private String role = "USER";

    @NotNull(message = "A data de nascimento do usuário não pode ficar vazia.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDay;

    @NotNull(message = "O CPF do usuário não pode ficar vazio.")
    @CPF(message = "O CPF do usuário não é valido!")
    private String cpf;

    public ApplicationUser(@NotNull ApplicationUser applicationUser) {
        this.password = applicationUser.getPassword();
        this.name = applicationUser.getName();
        this.email = applicationUser.getEmail();
        this.role = applicationUser.getRole();
        this.birthDay = applicationUser.getBirthDay();
        this.cpf = applicationUser.getCpf();
    }

}
