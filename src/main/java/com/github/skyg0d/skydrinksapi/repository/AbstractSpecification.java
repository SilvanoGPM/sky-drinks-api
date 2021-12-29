package com.github.skyg0d.skydrinksapi.repository;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public abstract class AbstractSpecification {

    protected static <T> Specification<T> withCreatedAt(String createdAt) {
        return getSpec(createdAt, (root, query, builder) -> (
                builder.equal(root.get("createdAt"), LocalDateTime.parse(createdAt))
        ));
    }

    protected static <T> Specification<T> withCreatedInDateOrAfter(String createdInDateOrAfter) {
        return getSpec(createdInDateOrAfter, (root, query, builder) -> (
                builder.greaterThanOrEqualTo(root.get("createdAt"), LocalDateTime.of(
                        LocalDate.parse(createdInDateOrAfter),
                        LocalTime.MIN
                ))
        ));
    }

    protected static <T> Specification<T> withCreatedInDateOrBefore(String createdInDateOrBefore) {
        return getSpec(createdInDateOrBefore, (root, query, builder) -> (
                builder.lessThanOrEqualTo(root.get("createdAt"), LocalDateTime.of(
                        LocalDate.parse(createdInDateOrBefore),
                        LocalTime.MAX
                ))
        ));
    }

    protected static String like(String string) {
        return "%" + string.toLowerCase() + "%";
    }

    protected static <T, E> Specification<E> getSpec(T value, Specification<E> spec) {
        if (value == null) {
            return null;
        }

        if (value instanceof String && ((String) value).isEmpty()) {
            return null;
        }

        return spec;
    }

}
