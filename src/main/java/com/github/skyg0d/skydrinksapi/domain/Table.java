package com.github.skyg0d.skydrinksapi.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.Positive;
import java.util.Set;

@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@javax.persistence.Table(name = "client_tables")
@Entity
public class Table extends BaseEntity {

    @Range(min = 1, max = 100, message = "O número de assentos de uma mesa deve estar entre 1 e 100.")
    @Positive(message = "O número de assentos em uma mesa deve ser positivo.")
    @Schema(description = "Número de assentos na mesa", example = "8")
    private int seats;

    @Positive(message = "O número da mesa deve ser positivo.")
    @Column(unique = true)
    @Schema(description = "Número da mesa", example = "3")
    private int number;

    @Schema(description = "Mesa ocupada", example = "true")
    private boolean occupied;

    @OneToMany(mappedBy = "table")
    @JsonBackReference
    @ToString.Exclude
    private Set<ClientRequest> requests;

}
