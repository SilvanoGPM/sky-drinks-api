package com.github.skyg0d.skydrinksapi.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

    @Transient
    @JsonIgnore
    @Autowired
    private EntityManager entityManager;

    @Size(min = 3, max = 100, message = "O nome da bebida precisa ter de 3 a 100 caracteres.")
    @NotBlank(message = "O nome da bebida não pode ficar vazio.")
    @Schema(description = "Nome do drink", example = "Blood Mary")
    private String name;

    @Positive(message = "O volume da bebida deve ser positivo.")
    @Schema(description = "O volume da bebida em mililitros.", example = "1000")
    private int volume;

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

    @ManyToMany(mappedBy = "drinks")
    @JsonBackReference
    @ToString.Exclude
    private Set<ClientRequest> requests;

    @Schema(description = "Adicionais do drink em formato de lista", example = "[gelo, limão]")
    public List<String> getAdditionalList() {
        return additional == null || additional.isEmpty()
                ? new ArrayList<>(Collections.emptyList())
                : new ArrayList<>(List.of(additional.split(ADDITIONAL_SEPARATOR)));
    }

}
