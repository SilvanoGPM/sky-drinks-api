package com.github.skyg0d.skydrinksapi.util.drink;

import com.github.skyg0d.skydrinksapi.domain.Drink;

import java.time.LocalDateTime;
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

}
