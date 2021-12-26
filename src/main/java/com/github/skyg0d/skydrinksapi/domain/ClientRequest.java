package com.github.skyg0d.skydrinksapi.domain;

import com.github.skyg0d.skydrinksapi.enums.ClientRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
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
    @Schema(description = "Mesa para entregar os drinks", example = "{ \"uuid\": \"35375453-5ff3-4c78-b458-00b5804afdfe\" }")
    private List<Drink> drinks;

    @NotNull(message = "Um pedido precisa conter um usuário.")
    @ManyToOne
    @JoinColumn(name = "user_uuid")
    @Schema(description = "Usuário que realizou o pedido", example = "{ \"uuid\": \"d9f7dbdd-4514-4f86-95af-0bba60228ef8\" }")
    private ApplicationUser user;

    @ManyToOne
    @JoinColumn(name = "table_uuid")
    @Schema(description = "Mesa para entregar os drinks", example = "{ \"uuid\": \"35375453-5ff3-4c78-b458-00b5804afdfe\" }")
    private Table table;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255) default 'PROCESSING'")
    @NotNull(message = "Um pedido precisa conter um status.")
    @Schema(description = "Status do pedido", example = "PROCESSING")
    private ClientRequestStatus status = ClientRequestStatus.PROCESSING;

    @PositiveOrZero(message = "O valor do pedido deve ser positivo ou igual a zero.")
    @Schema(description = "Valor total do pedido", example = "25.55")
    private double totalPrice;

}
