package com.github.skyg0d.skydrinksapi.requests;

import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.domain.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRequestPutRequestBody {

    @NotNull(message = "UUID do pedido não pode ficar vazio.")
    @Schema(description = "UUID do pedido", example = "27622ec9-e3c3-4bc1-a219-6b36922141df")
    private UUID uuid;

    @NotNull(message = "Um pedido precisa conter drinks.")
    @Schema(description = "Mesa para entregar os drinks", example = "{ \"uuid\": \"35375453-5ff3-4c78-b458-00b5804afdfe\" }")
    private List<Drink> drinks;

    @Schema(description = "Mesa para entregar os drinks", example = "{ \"uuid\": \"35375453-5ff3-4c78-b458-00b5804afdfe\" }")
    private Table table;

}
