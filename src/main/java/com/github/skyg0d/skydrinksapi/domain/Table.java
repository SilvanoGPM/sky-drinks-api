package com.github.skyg0d.skydrinksapi.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Positive;

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
    private int seats;

    @Positive(message = "O número da mesa deve ser positivo.")
    @Column(unique = true)
    private int number;

    private boolean occupied;

}
