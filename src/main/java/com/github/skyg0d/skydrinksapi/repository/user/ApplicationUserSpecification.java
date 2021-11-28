package com.github.skyg0d.skydrinksapi.repository.user;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.parameters.ApplicationUserParameters;
import com.github.skyg0d.skydrinksapi.repository.AbstractSpecification;
import org.springframework.data.jpa.domain.Specification;

import static org.springframework.data.jpa.domain.Specification.where;

public class ApplicationUserSpecification extends AbstractSpecification {

    public static Specification<ApplicationUser> getSpecification(ApplicationUserParameters applicationUserParameters) {
        return where(withName(applicationUserParameters.getName()))
                .and(where(withRole(applicationUserParameters.getRole())))
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

}
