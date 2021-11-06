package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.service.DrinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/drinks")
public class DrinkController {

    private final DrinkService drinkService;

    @GetMapping
    public ResponseEntity<Page<Drink>> listAll(Pageable pageable) {
        return ResponseEntity.ok(drinkService.listAll(pageable));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Drink> findById(@PathVariable UUID uuid) {
        return ResponseEntity.ok(drinkService.findByIdOrElseThrowBadRequestException(uuid));
    }

    @PostMapping
    public ResponseEntity<Drink> save(@RequestBody Drink drink) {
        return new ResponseEntity<>(drinkService.save(drink), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Void> replace(@RequestBody Drink drink) {
        drinkService.replace(drink);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        drinkService.delete(uuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
