package com.github.skyg0d.skydrinksapi.requests;

import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.domain.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRequestPostRequestBody {

    @NotNull(message = "Um pedido precisa conter drinks.")
    @Schema(description = "Lista de drinks do pedido", example = "[{ \"uuid\": \"a0ff11c9-0f4e-4852-9166-6dc72b331727\" }, { \"uuid\": \"ec7bb1ce-7770-4957-9dfe-1fa52b3ef340\" }]")
    private List<Drink> drinks;

    @Schema(description = "Mesa para entregar os drinks", example = "{ \"uuid\": \"35375453-5ff3-4c78-b458-00b5804afdfe\" }")
    private Table table;

    @Schema(description = "Pedido finalizado", example = "true")
    private boolean finished;

    @PositiveOrZero(message = "O valor do pedido deve ser positivo ou igual a zero.")
    @Schema(description = "Valor total do pedido", example = "25.55")
    private double totalPrice;

}
