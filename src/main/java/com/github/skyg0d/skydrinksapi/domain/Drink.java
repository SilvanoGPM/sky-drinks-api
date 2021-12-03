package com.github.skyg0d.skydrinksapi.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "drinks")
@Entity
public class Drink extends BaseEntity {

    public static final String ADDITIONAL_SEPARATOR = ";";

    @Size(min = 3, max = 100, message = "O nome da bebida precisa ter de 3 a 100 caracteres.")
    @NotBlank(message = "O nome da bebida não pode ficar vazio.")
    @Schema(description = "Nome do drink", example = "Blood Mary")
    private String name;

    @Schema(description = "Imagem do drink", example = "blood_mary.png")
    private String picture;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Descrição do drink", example = "Drink Refrescante")
    private String description;

    @Positive(message = "O valor da bebida deve ser positivo.")
    @Schema(description = "Preço do drink", example = "10.25")
    private double price;

    @Schema(description = "Drink alcoólico", example = "false")
    private boolean alcoholic;

    @Schema(description = "Adicionais do drink", example = "gelo;limão")
    private String additional;

    @Schema(description = "Adicionais do drink em formato de lista", example = "[gelo, limão]")
    public List<String> getAdditionalList() {
        return additional == null || additional.isEmpty()
                ? new ArrayList<>(Collections.emptyList())
                : new ArrayList<>(List.of(additional.split(ADDITIONAL_SEPARATOR)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Drink drink = (Drink) o;
        return getUuid() != null && Objects.equals(getUuid(), drink.getUuid());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
