package com.github.skyg0d.skydrinksapi.repository.drink;

import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.parameters.DrinkParameters;
import com.github.skyg0d.skydrinksapi.repository.AbstractSpecification;
import org.springframework.data.jpa.domain.Specification;

import static org.springframework.data.jpa.domain.Specification.where;

public class DrinkSpecification extends AbstractSpecification {

    public static Specification<Drink> getSpecification(DrinkParameters drinkParameters) {
        return where(withName(drinkParameters.getName()))
                .and(where(withAdditionals(drinkParameters.getAdditionals())))
                .and(where(withAlcoholic(drinkParameters.isAlcoholic())))
                .and(where(withPrice(drinkParameters.getPrice())))
                .and(where(withGreaterThanPrice(drinkParameters.getGreaterThanPrice())))
                .and(where(withLessThanPrice(drinkParameters.getLessThanPrice())))
                .and(where(withGreaterThanOrEqualToPrice(drinkParameters.getGreaterThanOrEqualToPrice())))
                .and(where(withLessThanOrEqualToPrice(drinkParameters.getLessThanOrEqualToPrice())))
                .and(where(withCreatedAt(drinkParameters.getCreatedAt())))
                .and(where(withCreatedInDateOrAfter(drinkParameters.getCreatedInDateOrAfter())))
                .and(where(withCreatedInDateOrBefore(drinkParameters.getCreatedInDateOrBefore())));
    }

    public static Specification<Drink> withName(String name) {
        return getSpec(name, (root, query, builder) -> (
                builder.like(builder.lower(root.get("name")), like(name))
        ));
    }

    public static Specification<Drink> withAdditionals(String additionals) {
        return getSpec(additionals, (root, query, builder) -> (
                builder.like(builder.lower(root.get("additionals")), like(additionals))
        ));
    }

    public static Specification<Drink> withAlcoholic(boolean alcoholic) {
        if (!alcoholic) return null;

        return getSpec(true, (root, query, builder) -> (
                builder.equal(root.get("alcoholic"), true)
        ));
    }

    public static Specification<Drink> withPrice(double price) {
        if (price < 0) return null;

        return getSpec(price, (root, query, builder) -> (
                builder.equal(root.get("price"), price)
        ));
    }

    public static Specification<Drink> withGreaterThanPrice(double greaterThanPrice) {
        if (greaterThanPrice < 0) return null;

        return getSpec(greaterThanPrice, (root, query, builder) -> (
                builder.greaterThan(root.get("price"), greaterThanPrice)
        ));
    }

    public static Specification<Drink> withLessThanPrice(double lessThanPrice) {
        if (lessThanPrice < 0) return null;

        return getSpec(lessThanPrice, (root, query, builder) -> (
                builder.lessThan(root.get("price"), lessThanPrice)
        ));
    }

    public static Specification<Drink> withGreaterThanOrEqualToPrice(double greaterThanOrEqualToPrice) {
        if (greaterThanOrEqualToPrice < 0) return null;

        return getSpec(greaterThanOrEqualToPrice, (root, query, builder) -> (
                builder.greaterThanOrEqualTo(root.get("price"), greaterThanOrEqualToPrice)
        ));
    }

    public static Specification<Drink> withLessThanOrEqualToPrice(double lessThanOrEqualToPrice) {
        if (lessThanOrEqualToPrice < 0) return null;

        return getSpec(lessThanOrEqualToPrice, (root, query, builder) -> (
                builder.lessThanOrEqualTo(root.get("price"), lessThanOrEqualToPrice)
        ));
    }

}
