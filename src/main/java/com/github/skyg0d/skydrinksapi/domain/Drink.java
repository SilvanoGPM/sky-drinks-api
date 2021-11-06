package com.github.skyg0d.skydrinksapi.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "drinks")
@Entity
public class Drink extends BaseEntity {

    private String name;

    private double price;

    private boolean alcoholic;

    private String additionals;

    public List<String> getAdditionals() {
        return List.of(additionals.split(";"));
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
