package com.github.skyg0d.skydrinksapi.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.br.CPF;

import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

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
    @Schema(description = "Nome do usuário", example = "Roger")
    private String name;

    @NotBlank(message = "O email do usuário não pode ficar vazio.")
    @Email(message = "O email não é válido.")
    @Column(unique = true)
    @Schema(description = "Email do usuário", example = "roger@mail.com")
    private String email;

    @NotBlank(message = "A senha do usuário não pode ficar vazia.")
    @Size(min = 8, message = "A senha precisa ter pelo menos 8 caracteres")
    @ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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
    @Column(unique = true)
    @Schema(description = "CPF do usuário", example = "123.456.789-10")
    private String cpf;

    @OneToMany(mappedBy = "user")
    @JsonBackReference
    @ToString.Exclude
    private Set<ClientRequest> requests;

    @Schema(description = "Caso verdadeiro, impede que o usuário realize novos pedidos", example = "true")
    private boolean lockRequests;

    @Schema(description = "Data em que o usuário foi impedido de realizar pedidos", example = "2004-04-09")
    private LocalDateTime lockRequestsTimestamp;

    public ApplicationUser(@NotNull ApplicationUser applicationUser) {
        this.password = applicationUser.getPassword();
        this.name = applicationUser.getName();
        this.email = applicationUser.getEmail();
        this.role = applicationUser.getRole();
        this.birthDay = applicationUser.getBirthDay();
        this.cpf = applicationUser.getCpf();
        this.requests = applicationUser.getRequests();
        this.lockRequests = applicationUser.isLockRequests();
        this.lockRequestsTimestamp = applicationUser.getLockRequestsTimestamp();
    }

}
