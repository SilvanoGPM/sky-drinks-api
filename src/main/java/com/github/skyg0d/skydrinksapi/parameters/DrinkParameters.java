package com.github.skyg0d.skydrinksapi.parameters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DrinkParameters {

     private String name;

     private String additionals;

     private String createdAt;

     private String createdInDateOrAfter;

     private String createdInDateOrBefore;

     private boolean alcoholic;

     private double price = -1;

     private double greaterThanPrice = -1;

     private double lessThanPrice = -1;

     private double greaterThanOrEqualToPrice = -1;

     private double lessThanOrEqualToPrice = -1;

}
