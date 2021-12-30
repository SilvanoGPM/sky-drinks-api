package com.github.skyg0d.skydrinksapi.util.drink;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.domain.Table;
import com.github.skyg0d.skydrinksapi.enums.ClientRequestStatus;
import com.github.skyg0d.skydrinksapi.util.table.TableCreator;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserCreator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DrinkCreator {

    public static final UUID uuid = UUID.fromString("74a49e0b-9e36-4af4-b283-d569813047a6");

    public static Drink createDrinkToBeSave() {
        return Drink
                .builder()
                .name("Suco de laranja com raspas de limão")
                .volume(400)
                .description("Suco básico e refrescante de laranja com algumas raspas de limão.")
                .additional("gelo;limão")
                .alcoholic(false)
                .price(6.25)
                .picture("suco_de_laranja_com_raspas_de_limao.png")
                .build();
    }

    public static Drink createValidDrink() {
        return Drink
                .builder()
                .uuid(uuid)
                .requests(createClientRequest())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .name("Suco de laranja com raspas de limão")
                .volume(400)
                .description("Suco básico e refrescante de laranja com algumas raspas de limão.")
                .additional("gelo;limão")
                .alcoholic(false)
                .price(6.25)
                .picture("suco_de_laranja_com_raspas_de_limao.png")
                .build();
    }

    public static Drink createValidUpdatedDrink() {
        return Drink
                .builder()
                .uuid(uuid)
                .requests(createClientRequest())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .name("Suco de laranja com limão")
                .volume(450)
                .description("Suco básico e refrescante de laranja com algumas raspas de limão.")
                .additional("gelo")
                .alcoholic(false)
                .price(6.55)
                .picture("suco_de_laranja_com_limão.png")
                .build();
    }

    private static Set<ClientRequest> createClientRequest() {
        ApplicationUser user = ApplicationUser
                .builder()
                .uuid(ApplicationUserCreator.uuid)
                .name("User Minor")
                .role("USER")
                .cpf("512.262.484-46")
                .email("userminor@mail.com")
                .password("$2a$10$3eqv7nOc.CFIcOa7zpkwV.h/Jt4h0io6qAha8X/4zOeeexRi6afn2")
                .birthDay(LocalDate.now())
                .build();

        Table table = Table
                .builder()
                .uuid(TableCreator.uuid)
                .number(1)
                .seats(4)
                .occupied(true)
                .build();

        ArrayList<Drink> drinks = new ArrayList<>(List.of(
                Drink
                        .builder()
                        .uuid(uuid)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .name("Suco de laranja com raspas de limão")
                        .volume(400)
                        .description("Suco básico e refrescante de laranja com algumas raspas de limão.")
                        .additional("gelo;limão")
                        .alcoholic(false)
                        .price(6.25)
                        .picture("suco_de_laranja_com_raspas_de_limao.png")
                        .build()
        ));

        return Set.of(ClientRequest
                .builder()
                .user(user)
                .drinks(drinks)
                .totalPrice(drinks.get(0).getPrice())
                .status(ClientRequestStatus.PROCESSING)
                .table(table)
                .build());
    }

}
