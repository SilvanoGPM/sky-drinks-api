package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.exception.BadRequestException;
import com.github.skyg0d.skydrinksapi.repository.DrinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DrinkService {

    private final DrinkRepository drinkRepository;

    public Page<Drink> listAll(Pageable pageable) {
        return drinkRepository.findAll(pageable);
    }

    public Drink findByIdOrElseThrowBadRequestException(UUID uuid) {
        return drinkRepository
                .findById(uuid)
                .orElseThrow(() -> new BadRequestException(String.format("Bebida com id: %s, não foi encontrada.", uuid)));
    }

    public Drink save(Drink drink) {
        return drinkRepository.save(drink);
    }

    public void replace(Drink drink) {
        findByIdOrElseThrowBadRequestException(drink.getUuid());
        drinkRepository.save(drink);
    }

    public void delete(UUID uuid) {
        drinkRepository.delete(findByIdOrElseThrowBadRequestException(uuid));
    }

}
