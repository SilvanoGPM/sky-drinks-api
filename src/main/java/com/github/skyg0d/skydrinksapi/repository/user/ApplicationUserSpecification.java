package com.github.skyg0d.skydrinksapi.repository.user;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.parameters.ApplicationUserParameters;
import com.github.skyg0d.skydrinksapi.repository.AbstractSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

import static org.springframework.data.jpa.domain.Specification.where;

public class ApplicationUserSpecification extends AbstractSpecification {

    public static Specification<ApplicationUser> getSpecification(ApplicationUserParameters applicationUserParameters) {
        return where(withName(applicationUserParameters.getName()))
                .and(where(withRole(applicationUserParameters.getRole())))
                .and(where(withBirthDay(applicationUserParameters.getBirthDay())))
                .and(where(withBirthInDateOrAfter(applicationUserParameters.getBirthInDateOrAfter())))
                .and(where(withBirthInDateOrBefore(applicationUserParameters.getBirthInDateOrBefore())))
                .and(where(withCreatedAt(applicationUserParameters.getCreatedAt())))
                .and(where(withCreatedInDateOrAfter(applicationUserParameters.getCreatedInDateOrAfter())))
                .and(where(withCreatedInDateOrBefore(applicationUserParameters.getCreatedInDateOrBefore())));
    }

    public static Specification<ApplicationUser> withName(String name) {
        return getSpec(name, (root, query, builder) -> (
                builder.like(builder.lower(root.get("name")), like(name))
        ));
    }

    public static Specification<ApplicationUser> withRole(String role) {
        return getSpec(role, (root, query, builder) -> (
                builder.like(builder.lower(root.get("role")), like(role))
        ));
    }

    public static Specification<ApplicationUser> withBirthDay(String birth) {
        return getSpec(birth, (root, query, builder) -> (
                builder.equal(root.get("birthDay"), LocalDate.parse(birth))
        ));
    }

    public static Specification<ApplicationUser> withBirthInDateOrAfter(String birth) {
        return getSpec(birth, (root, query, builder) -> (
                builder.greaterThanOrEqualTo(root.get("birthDay"), LocalDate.parse(birth))
        ));
    }

    public static Specification<ApplicationUser> withBirthInDateOrBefore(String birth) {
        return getSpec(birth, (root, query, builder) -> (
                builder.lessThanOrEqualTo(root.get("birthDay"), LocalDate.parse(birth))
        ));
    }

    public static Specification<ApplicationUser> getStaffUsers() {
        return ((root, query, builder) -> (
                builder.notLike(builder.lower(root.get("role")), "user")
        ));
    }

}
