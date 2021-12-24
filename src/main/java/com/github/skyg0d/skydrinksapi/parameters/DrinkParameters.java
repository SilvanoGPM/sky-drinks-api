package com.github.skyg0d.skydrinksapi.parameters;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DrinkParameters {

     @Parameter(description = "Nome do drink para pesquisa", example = "Blood Mary", allowEmptyValue = true)
     private String name;

     @Parameter(description = "Volume do drink para pesquisa", example = "1000", allowEmptyValue = true)
     private int volume = -1;

     @Parameter(description = "Volume do drink ou maior que isso", example = "1000", allowEmptyValue = true)
     private double greaterThanVolume = -1;

     @Parameter(description = "Volume do drink ou menor que isso", example = "1000", allowEmptyValue = true)
     private double lessThanVolume = -1;

     @Parameter(description = "Volume do drink, maior que ou igual a isso", example = "1000", allowEmptyValue = true)
     private double greaterThanOrEqualToVolume = -1;

     @Parameter(description = "Volume do drink,menor que ou igual a isso", example = "1000", allowEmptyValue = true)
     private double lessThanOrEqualToVolume = -1;

     @Parameter(description = "Descrição do drink para pesquisa", example = "Drink Refrescante", allowEmptyValue = true)
     private String description;

     @Parameter(description = "Adicionais do drink para pesquisa", example = "gelo;limão", allowEmptyValue = true)
     private String additional;

     @Parameter(description = "Data que o drink foi criado", example = "2004-04-04T10:16:28.043216", allowEmptyValue = true)
     private String createdAt;

     @Parameter(description = "Data que o drink foi criado ou depois", example = "2004-04-04", allowEmptyValue = true)
     private String createdInDateOrAfter;

     @Parameter(description = "Data que o drink foi criado ou antes", example = "2004-04-04", allowEmptyValue = true)
     private String createdInDateOrBefore;

     @Parameter(
             description = "Se o valor for igual a um, pesquisará todos os drinks alcoólicos, caso seja zero, pesquisa todos os pedidos não alcoólicos, e caso seja um menos um, pesquisa todos os drinks",
             example = "1",
             allowEmptyValue = true
     )
     private int alcoholic = -1;

     @Parameter(description = "Preço do drink", example = "10.25", allowEmptyValue = true)
     private double price = -1;

     @Parameter(description = "Preço do drink ou maior que isso", example = "10.25", allowEmptyValue = true)
     private double greaterThanPrice = -1;

     @Parameter(description = "Preço do drink ou menor que isso", example = "10.25", allowEmptyValue = true)
     private double lessThanPrice = -1;

     @Parameter(description = "Preço do drink, maior que ou igual a isso", example = "10.25", allowEmptyValue = true)
     private double greaterThanOrEqualToPrice = -1;

     @Parameter(description = "Preço do drink,menor que ou igual a isso", example = "10.25", allowEmptyValue = true)
     private double lessThanOrEqualToPrice = -1;

}
