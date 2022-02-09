package com.github.skyg0d.skydrinksapi.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "password_reset")
@Entity
public class PasswordReset extends BaseEntity {

    @NotBlank(message = "O código de confirmação não pode ficar vazio.")
    @Schema(description = "Código de confirmação para restaurar senha", example = "qfq71VC13")
    private String token;

    @NotNull(message = "Data de expiração do código de confirmação não pode ficar vazia")
    @Schema(description = "Data que o código de confirmação expira")
    private LocalDateTime expireDate;

    @NotNull(message = "Usuário não pode ficar vazio")
    @OneToOne
    @Schema(description = "Usuário para restaurar senha")
    private ApplicationUser user;

    @Schema(description = "Indicia se o pedido de alteração já foi finalizado")
    private boolean resetFinished;
    
}
