package com.github.skyg0d.skydrinksapi.repository.table;

import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.domain.Table;
import com.github.skyg0d.skydrinksapi.parameters.DrinkParameters;
import com.github.skyg0d.skydrinksapi.parameters.TableParameters;
import com.github.skyg0d.skydrinksapi.repository.AbstractSpecification;
import org.springframework.data.jpa.domain.Specification;

import static org.springframework.data.jpa.domain.Specification.where;

public class TableSpecification extends AbstractSpecification {

    public static Specification<Table> getSpecification(TableParameters tableParameters) {
        return where(withOccupied(tableParameters.getOccupied()))
                .and(where(withSeats(tableParameters.getSeats())))
                .and(where(withGreaterThanSeats(tableParameters.getGreaterThanSeats())))
                .and(where(withLessThanSeats(tableParameters.getLessThanSeats())))
                .and(where(withGreaterThanOrEqualToSeats(tableParameters.getGreaterThanOrEqualToSeats())))
                .and(where(withLessThanOrEqualToSeats(tableParameters.getLessThanOrEqualToSeats())))
                .and(where(withCreatedAt(tableParameters.getCreatedAt())))
                .and(where(withCreatedInDateOrAfter(tableParameters.getCreatedInDateOrAfter())))
                .and(where(withCreatedInDateOrBefore(tableParameters.getCreatedInDateOrBefore())));
    }

    public static Specification<Table> withOccupied(int occupied) {
        if (occupied != 0 && occupied != 1) return null;

        boolean isOccupied = occupied == 1;

        return getSpec(isOccupied, (root, query, builder) -> (
                builder.equal(root.get("occupied"), isOccupied)
        ));
    }

    public static Specification<Table> withSeats(double seats) {
        if (seats < 0) return null;

        return getSpec(seats, (root, query, builder) -> (
                builder.equal(root.get("seats"), seats)
        ));
    }

    public static Specification<Table> withGreaterThanSeats(double greaterThanSeats) {
        if (greaterThanSeats < 0) return null;

        return getSpec(greaterThanSeats, (root, query, builder) -> (
                builder.greaterThan(root.get("seats"), greaterThanSeats)
        ));
    }

    public static Specification<Table> withLessThanSeats(double lessThanSeats) {
        if (lessThanSeats < 0) return null;

        return getSpec(lessThanSeats, (root, query, builder) -> (
                builder.lessThan(root.get("seats"), lessThanSeats)
        ));
    }

    public static Specification<Table> withGreaterThanOrEqualToSeats(double greaterThanOrEqualToSeats) {
        if (greaterThanOrEqualToSeats < 0) return null;

        return getSpec(greaterThanOrEqualToSeats, (root, query, builder) -> (
                builder.greaterThanOrEqualTo(root.get("seats"), greaterThanOrEqualToSeats)
        ));
    }

    public static Specification<Table> withLessThanOrEqualToSeats(double lessThanOrEqualToSeats) {
        if (lessThanOrEqualToSeats < 0) return null;

        return getSpec(lessThanOrEqualToSeats, (root, query, builder) -> (
                builder.lessThanOrEqualTo(root.get("seats"), lessThanOrEqualToSeats)
        ));
    }


}
