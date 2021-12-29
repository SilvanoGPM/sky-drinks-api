package com.github.skyg0d.skydrinksapi.util.request;

import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.enums.ClientRequestStatus;
import com.github.skyg0d.skydrinksapi.util.drink.DrinkCreator;
import com.github.skyg0d.skydrinksapi.util.table.TableCreator;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserCreator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientRequestCreator {

    private static final UUID uuid = UUID.fromString("da278fd9-cc0a-464b-97ad-9a5215fb8fcf");

    public static ClientRequest createClientRequestToBeSave() {
        return ClientRequest
                .builder()
                .user(ApplicationUserCreator.createValidApplicationUser())
                .table(TableCreator.createValidTable())
                .drinks(new ArrayList<>(List.of(DrinkCreator.createValidDrink())))
                .totalPrice(0.0)
                .status(ClientRequestStatus.PROCESSING)
                .build();
    }

    public static ClientRequest createValidClientRequest() {
        ArrayList<Drink> drinks = new ArrayList<>(List.of(DrinkCreator.createValidDrink()));

        double totalPrice = calculatePrice(drinks);

        return ClientRequest
                .builder()
                .uuid(uuid)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(ApplicationUserCreator.createValidApplicationUser())
                .drinks(drinks)
                .table(TableCreator.createValidTable())
                .totalPrice(totalPrice)
                .status(ClientRequestStatus.PROCESSING)
                .build();
    }

    public static ClientRequest createValidUpdatedClientRequest() {
        ArrayList<Drink> drinks = new ArrayList<>(List.of(DrinkCreator.createValidDrink()));

        double totalPrice = calculatePrice(drinks);

        return ClientRequest
                .builder()
                .uuid(uuid)
                .updatedAt(LocalDateTime.now())
                .user(ApplicationUserCreator.createValidApplicationUser())
                .drinks(drinks)
                .table(TableCreator.createValidTable())
                .totalPrice(totalPrice)
                .status(ClientRequestStatus.FINISHED)
                .build();
    }

    public static ClientRequest createClientRequestFinished() {
        ArrayList<Drink> drinks = new ArrayList<>(List.of(DrinkCreator.createValidDrink()));

        double totalPrice = calculatePrice(drinks);

        return ClientRequest
                .builder()
                .uuid(uuid)
                .updatedAt(LocalDateTime.now())
                .user(ApplicationUserCreator.createValidApplicationUser())
                .drinks(drinks)
                .table(TableCreator.createValidTable())
                .totalPrice(totalPrice)
                .status(ClientRequestStatus.FINISHED)
                .build();
    }

    public static ClientRequest createClientRequestCanceled() {
        ArrayList<Drink> drinks = new ArrayList<>(List.of(DrinkCreator.createValidDrink()));

        double totalPrice = calculatePrice(drinks);

        return ClientRequest
                .builder()
                .uuid(uuid)
                .updatedAt(LocalDateTime.now())
                .user(ApplicationUserCreator.createValidApplicationUser())
                .drinks(drinks)
                .table(TableCreator.createValidTable())
                .totalPrice(totalPrice)
                .status(ClientRequestStatus.CANCELED)
                .build();
    }

    public static ClientRequest createClientRequestDelivered() {
        ArrayList<Drink> drinks = new ArrayList<>(List.of(DrinkCreator.createValidDrink()));

        double totalPrice = calculatePrice(drinks);

        return ClientRequest
                .builder()
                .uuid(uuid)
                .updatedAt(LocalDateTime.now())
                .user(ApplicationUserCreator.createValidApplicationUser())
                .drinks(drinks)
                .table(TableCreator.createValidTable())
                .totalPrice(totalPrice)
                .status(ClientRequestStatus.FINISHED)
                .delivered(true)
                .build();
    }

    private static double calculatePrice(List<Drink> drinks) {
        return drinks
                .stream()
                .mapToDouble(Drink::getPrice)
                .sum();
    }

}
