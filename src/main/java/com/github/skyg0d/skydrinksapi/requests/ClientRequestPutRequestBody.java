package com.github.skyg0d.skydrinksapi.requests;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.domain.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRequestPutRequestBody {

    @NotNull(message = "UUID do pedido não pode ficar vazio.")
    private UUID uuid;

    @NotNull(message = "Um pedido precisa conter drinks.")
    private List<Drink> drinks;

    @NotNull(message = "Um pedido precisa conter um usuário.")
    private ApplicationUser user;

    private Table table;

    private boolean finished;

    @PositiveOrZero(message = "O valor do pedido deve ser positivo ou igual a zero.")
    private double totalPrice;

}
