package com.github.skyg0d.skydrinksapi.repository;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public abstract class AbstractSpecification {

    protected static <T> Specification<T> withCreatedAt(String createdAt) {
        return getSpec(createdAt, (root, query, builder) -> (
                builder.equal(root.get("createdAt"), formatRequestedDate(createdAt))
        ));
    }

    protected static <T> Specification<T> withCreatedInDateOrAfter(String createdInDateOrAfter) {
        return getSpec(createdInDateOrAfter, (root, query, builder) -> (
                builder.greaterThanOrEqualTo(root.get("createdAt"), formatRequestedDate(createdInDateOrAfter))
        ));
    }

    protected static <T> Specification<T> withCreatedInDateOrBefore(String createdInDateOrBefore) {
        return getSpec(createdInDateOrBefore, (root, query, builder) -> (
                builder.lessThanOrEqualTo(root.get("createdAt"), formatRequestedDate(createdInDateOrBefore))
        ));
    }

    protected static LocalDateTime formatRequestedDate(String date) {
        return LocalDateTime.parse(date.replace("_", ":"));
    }

    protected static String like(String string) {
        return "%" + string.toLowerCase() + "%";
    }

    protected static <T, E> Specification<E> getSpec(T value, Specification<E> spec) {
        return value != null ? spec : null;
    }

}
