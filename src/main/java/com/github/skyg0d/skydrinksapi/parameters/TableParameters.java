package com.github.skyg0d.skydrinksapi.parameters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TableParameters {

    private String createdAt;

    private String createdInDateOrAfter;

    private String createdInDateOrBefore;

    private int occupied = -1;

    private int seats = -1;

    private int greaterThanSeats = -1;

    private int lessThanSeats = -1;

    private int greaterThanOrEqualToSeats = -1;

    private int lessThanOrEqualToSeats = -1;

}
