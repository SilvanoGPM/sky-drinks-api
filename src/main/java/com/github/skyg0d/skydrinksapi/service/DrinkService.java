package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.exception.BadRequestException;
import com.github.skyg0d.skydrinksapi.mapper.DrinkMapper;
import com.github.skyg0d.skydrinksapi.parameters.DrinkParameters;
import com.github.skyg0d.skydrinksapi.repository.drink.DrinkRepository;
import com.github.skyg0d.skydrinksapi.repository.drink.DrinkSpecification;
import com.github.skyg0d.skydrinksapi.repository.request.ClientRequestRepository;
import com.github.skyg0d.skydrinksapi.requests.DrinkPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.DrinkPutRequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;

@Log4j2
@Service
@RequiredArgsConstructor
public class DrinkService {

    private final DrinkRepository drinkRepository;
    private final ClientRequestRepository clientRequestRepository;
    private final DrinkMapper mapper = DrinkMapper.INSTANCE;

    public Page<Drink> listAll(Pageable pageable) {
        return drinkRepository.findAll(pageable);
    }

    public Drink findByIdOrElseThrowBadRequestException(UUID uuid) {
        return drinkRepository
                .findById(uuid)
                .orElseThrow(() -> new BadRequestException(String.format("Bebida com id: %s, não foi encontrada.", uuid)));
    }

    public List<Drink> findByPicture(String picture) {
        return drinkRepository.findByPicture(picture);
    }

    public Page<Drink> search(DrinkParameters drinkParameters, Pageable pageable) {
        return drinkRepository.findAll(DrinkSpecification.getSpecification(drinkParameters), pageable);
    }

    public Drink save(DrinkPostRequestBody drinkPostRequestBody) {
        return drinkRepository.save(mapper.toDrink(drinkPostRequestBody));
    }

    public void replace(DrinkPutRequestBody drinkPutRequestBody) {
        findByIdOrElseThrowBadRequestException(drinkPutRequestBody.getUuid());

        drinkRepository.save(mapper.toDrink(drinkPutRequestBody));
    }

    public void delete(UUID uuid) {
        Drink drinkFound = findByIdOrElseThrowBadRequestException(uuid);

        Set<ClientRequest> requests = drinkFound.getRequests();

        if (requests != null && !requests.isEmpty()) {
            Predicate<Drink> isDrinkFound = (drink) -> drink.equals(drinkFound);

            for (ClientRequest request : requests) {
                List<Drink> drinks = request.getDrinks();

                boolean onlyThisDrinkInRequest = drinks.stream().allMatch(isDrinkFound);

                drinks.removeIf(isDrinkFound);

                if (onlyThisDrinkInRequest) {
                    clientRequestRepository.delete(request);
                }
            }
        }

        drinkRepository.delete(drinkFound);
    }

}
