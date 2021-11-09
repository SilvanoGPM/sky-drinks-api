package com.github.skyg0d.skydrinksapi.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@javax.persistence.Table(name = "client_requests")
@Entity
public class ClientRequest extends BaseEntity {

    @ToString.Exclude
    @NotNull(message = "Um pedido precisa conter drinks.")
    @ManyToMany
    @JoinTable(
            name = "request_drink",
            joinColumns = @JoinColumn(name = "request_id"),
            inverseJoinColumns = @JoinColumn(name = "drink_id")
    )
    private List<Drink> drinks;

    @ManyToOne
    @JoinColumn(name = "table_uuid")
    private Table table;

    private boolean finished;

    @PositiveOrZero(message = "O valor do pedido deve ser positivo ou igual a zero.")
    private double totalPrice;

}
