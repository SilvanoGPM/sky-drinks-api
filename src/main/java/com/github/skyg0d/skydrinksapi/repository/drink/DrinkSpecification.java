package com.github.skyg0d.skydrinksapi.repository.drink;

import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.parameters.DrinkParameters;
import com.github.skyg0d.skydrinksapi.repository.AbstractSpecification;
import org.springframework.data.jpa.domain.Specification;

import static org.springframework.data.jpa.domain.Specification.where;

public class DrinkSpecification extends AbstractSpecification {

    public static Specification<Drink> getSpecification(DrinkParameters drinkParameters) {
        return where(withName(drinkParameters.getName()))
                .and(where(withVolume(drinkParameters.getVolume())))
                .and(where(withGreaterThanVolume(drinkParameters.getGreaterThanVolume())))
                .and(where(withLessThanVolume(drinkParameters.getLessThanVolume())))
                .and(where(withGreaterThanOrEqualToVolume(drinkParameters.getGreaterThanOrEqualToVolume())))
                .and(where(withLessThanOrEqualToVolume(drinkParameters.getLessThanOrEqualToVolume())))
                .and(where(withDescription(drinkParameters.getDescription())))
                .and(where(withAdditional(drinkParameters.getAdditional())))
                .and(where(withAlcoholic(drinkParameters.getAlcoholic())))
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

    public static Specification<Drink> withVolume(double volume) {
        if (volume < 0) return null;

        return getSpec(volume, (root, query, builder) -> (
                builder.equal(root.get("volume"), volume)
        ));
    }

    public static Specification<Drink> withGreaterThanVolume(double greaterThanVolume) {
        if (greaterThanVolume < 0) return null;

        return getSpec(greaterThanVolume, (root, query, builder) -> (
                builder.greaterThan(root.get("volume"), greaterThanVolume)
        ));
    }

    public static Specification<Drink> withLessThanVolume(double lessThanVolume) {
        if (lessThanVolume < 0) return null;

        return getSpec(lessThanVolume, (root, query, builder) -> (
                builder.lessThan(root.get("volume"), lessThanVolume)
        ));
    }

    public static Specification<Drink> withGreaterThanOrEqualToVolume(double greaterThanOrEqualToVolume) {
        if (greaterThanOrEqualToVolume < 0) return null;

        return getSpec(greaterThanOrEqualToVolume, (root, query, builder) -> (
                builder.greaterThanOrEqualTo(root.get("volume"), greaterThanOrEqualToVolume)
        ));
    }

    public static Specification<Drink> withLessThanOrEqualToVolume(double lessThanOrEqualToVolume) {
        if (lessThanOrEqualToVolume < 0) return null;

        return getSpec(lessThanOrEqualToVolume, (root, query, builder) -> (
                builder.lessThanOrEqualTo(root.get("volume"), lessThanOrEqualToVolume)
        ));
    }

    public static Specification<Drink> withDescription(String description) {
        return getSpec(description, (root, query, builder) -> (
                builder.like(builder.lower(root.get("description")), like(description))
        ));
    }

    public static Specification<Drink> withAdditional(String additional) {
        return getSpec(additional, (root, query, builder) -> (
                builder.like(builder.lower(root.get("additional")), like(additional))
        ));
    }

    public static Specification<Drink> withAlcoholic(int alcoholic) {
        if (alcoholic != 0 && alcoholic != 1) return null;

        boolean isAlcoholic = alcoholic == 1;

        return getSpec(isAlcoholic, (root, query, builder) -> (
                builder.equal(root.get("alcoholic"), isAlcoholic)
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
