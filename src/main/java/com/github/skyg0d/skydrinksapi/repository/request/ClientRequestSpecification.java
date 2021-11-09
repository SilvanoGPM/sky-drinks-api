package com.github.skyg0d.skydrinksapi.repository.request;

import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.parameters.ClientRequestParameters;
import com.github.skyg0d.skydrinksapi.repository.AbstractSpecification;
import com.github.skyg0d.skydrinksapi.util.UUIDUtil;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

import static org.springframework.data.jpa.domain.Specification.where;

public class ClientRequestSpecification extends AbstractSpecification {

    private final static UUIDUtil uuidUtil = new UUIDUtil();

    public static Specification<ClientRequest> getSpecification(ClientRequestParameters parameters) {
        return where(withFinished(parameters.getFinished()))
                .and(where(withDrinkUUID(parameters.getDrinkUUID())))
                .and(where(withTableUUID(parameters.getTableUUID())))
                .and(where(withTotalPrice(parameters.getTotalPrice())))
                .and(where(withGreaterThanTotalPrice(parameters.getGreaterThanTotalPrice())))
                .and(where(withLessThanTotalPrice(parameters.getLessThanTotalPrice())))
                .and(where(withGreaterThanOrEqualToTotalPrice(parameters.getGreaterThanOrEqualToTotalPrice())))
                .and(where(withLessThanOrEqualToTotalPrice(parameters.getLessThanOrEqualToTotalPrice())))
                .and(where(withCreatedAt(parameters.getCreatedAt())))
                .and(where(withCreatedInDateOrAfter(parameters.getCreatedInDateOrAfter())))
                .and(where(withCreatedInDateOrBefore(parameters.getCreatedInDateOrBefore())));
    }

    public static Specification<ClientRequest> withDrinkUUID(UUID uuid) {
        return getSpec(uuid, (root, query, builder) -> (
                builder.equal(root.join("drinks").get("uuid"), uuid)
        ));
    }

    public static Specification<ClientRequest> withTableUUID(String stringUUID) {
        if (stringUUID.equals("null")) {
            return (root, query, builder) -> builder.isNull(root.get("table"));
        }

        UUID uuid = uuidUtil.getUUID(stringUUID);

        return getSpec(uuid, (root, query, builder) -> (
                builder.equal(root.join("table").get("uuid"), uuid)
        ));
    }

    public static Specification<ClientRequest> withFinished(int finished) {
        if (finished < 0) return null;

        boolean isFinished = finished == 1;

        return getSpec(isFinished, (root, query, builder) -> (
                builder.equal(root.get("finished"), isFinished)
        ));
    }

    public static Specification<ClientRequest> withTotalPrice(double totalPrice) {
        if (totalPrice < 0) return null;

        return getSpec(totalPrice, (root, query, builder) -> (
                builder.equal(root.get("totalPrice"), totalPrice)
        ));
    }

    public static Specification<ClientRequest> withGreaterThanTotalPrice(double greaterThanTotalPrice) {
        if (greaterThanTotalPrice < 0) return null;

        return getSpec(greaterThanTotalPrice, (root, query, builder) -> (
                builder.greaterThan(root.get("totalPrice"), greaterThanTotalPrice)
        ));
    }

    public static Specification<ClientRequest> withLessThanTotalPrice(double lessThanTotalPrice) {
        if (lessThanTotalPrice < 0) return null;

        return getSpec(lessThanTotalPrice, (root, query, builder) -> (
                builder.lessThan(root.get("totalPrice"), lessThanTotalPrice)
        ));
    }

    public static Specification<ClientRequest> withGreaterThanOrEqualToTotalPrice(double greaterThanOrEqualToTotalPrice) {
        if (greaterThanOrEqualToTotalPrice < 0) return null;

        return getSpec(greaterThanOrEqualToTotalPrice, (root, query, builder) -> (
                builder.greaterThanOrEqualTo(root.get("totalPrice"), greaterThanOrEqualToTotalPrice)
        ));
    }

    public static Specification<ClientRequest> withLessThanOrEqualToTotalPrice(double lessThanOrEqualToTotalPrice) {
        if (lessThanOrEqualToTotalPrice < 0) return null;

        return getSpec(lessThanOrEqualToTotalPrice, (root, query, builder) -> (
                builder.lessThanOrEqualTo(root.get("totalPrice"), lessThanOrEqualToTotalPrice)
        ));
    }

}
