package com.github.skyg0d.skydrinksapi.repository.drink;

import com.github.skyg0d.skydrinksapi.domain.Drink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface DrinkRepository extends JpaRepository<Drink, UUID>, JpaSpecificationExecutor<Drink> {
}
