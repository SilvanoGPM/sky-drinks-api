package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.parameters.DrinkParameters;
import com.github.skyg0d.skydrinksapi.requests.DrinkPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.DrinkPutRequestBody;
import com.github.skyg0d.skydrinksapi.service.DrinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/drinks")
@Log4j2
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

    @GetMapping("/search")
    public ResponseEntity<Page<Drink>> search(DrinkParameters drinkParameters, Pageable pageable) {
        return ResponseEntity.ok(drinkService.search(drinkParameters, pageable));
    }

    @PostMapping("/barmen")
    public ResponseEntity<Drink> save(@RequestBody @Valid DrinkPostRequestBody drinkPostRequestBody) {
        return new ResponseEntity<>(drinkService.save(drinkPostRequestBody), HttpStatus.CREATED);
    }

    @PutMapping("/barmen")
    public ResponseEntity<Void> replace(@RequestBody @Valid DrinkPutRequestBody drinkPutRequestBody) {
        drinkService.replace(drinkPutRequestBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/barmen/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        drinkService.delete(uuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
