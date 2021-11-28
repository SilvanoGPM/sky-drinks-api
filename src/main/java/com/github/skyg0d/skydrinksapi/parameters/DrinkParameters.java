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

     private String description;

     private String additional;

     private String createdAt;

     private String createdInDateOrAfter;

     private String createdInDateOrBefore;

     private int alcoholic = -1;

     private double price = -1;

     private double greaterThanPrice = -1;

     private double lessThanPrice = -1;

     private double greaterThanOrEqualToPrice = -1;

     private double lessThanOrEqualToPrice = -1;

}
