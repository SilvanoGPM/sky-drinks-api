package com.github.skyg0d.skydrinksapi.parameters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ClientRequestParameters {

    private UUID drinkUUID;

    private String drinkName;

    private String drinkDescription;

    private UUID userUUID;

    private String userName;

    private String userEmail;

    private String userCpf;

    private String tableUUID = "";

    private String createdAt;

    private String createdInDateOrAfter;

    private String createdInDateOrBefore;

    private int finished = -1;

    private double totalPrice = -1;

    private double greaterThanTotalPrice = -1;

    private double lessThanTotalPrice = -1;

    private double greaterThanOrEqualToTotalPrice = -1;

    private double lessThanOrEqualToTotalPrice = -1;

}
