package com.github.skyg0d.skydrinksapi.controller;

import com.github.skyg0d.skydrinksapi.domain.Table;
import com.github.skyg0d.skydrinksapi.parameters.TableParameters;
import com.github.skyg0d.skydrinksapi.requests.TablePostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.TablePutRequestBody;
import com.github.skyg0d.skydrinksapi.service.TableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tables")
public class TableController {

    private final TableService tableService;

    @GetMapping("/waiter")
    @Operation(summary = "Retorna todos as mesas com paginação", tags = "Tables")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<Page<Table>> listAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(tableService.listAll(pageable));
    }

    @GetMapping("/waiter/{uuid}")
    @Operation(summary = "Retorna a mesa especificada", tags = "Tables")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando a mesa não existe no banco de dados"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<Table> findById(@PathVariable UUID uuid) {
        return ResponseEntity.ok(tableService.findByIdOrElseThrowBadRequestException(uuid));
    }


    @GetMapping("/waiter/search")
    @Operation(summary = "Retorna as mesas encontradas com paginação", tags = "Tables")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<Page<Table>> search(@ParameterObject TableParameters tableParameters, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(tableService.search(tableParameters, pageable));
    }

    @GetMapping("/waiter/find-by-number/{number}")
    @Operation(summary = "Retorna a mesa especificada", tags = "Tables")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando a mesa não existe no banco de dados"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<Table> findByNumber(@PathVariable int number) {
        return ResponseEntity.ok(tableService.findByNumberOrElseThrowBadRequestException(number));
    }

    @PostMapping("/waiter")
    @Operation(summary = "Cria uma nova mesa e retorna seus dados", tags = "Tables")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<Table> save(@RequestBody @Valid TablePostRequestBody tablePostRequestBody) {
        return new ResponseEntity<>(tableService.save(tablePostRequestBody), HttpStatus.CREATED);
    }

    @PutMapping("/waiter")
    @Operation(summary = "Atualiza uma mesa", tags = "Tables")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando a mesa não existe no banco de dados"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<Void> replace(@RequestBody @Valid TablePutRequestBody tablePutRequestBody) {
        tableService.replace(tablePutRequestBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/waiter/switch-occupied/{identification}")
    @Operation(summary = "Retorna a mesa especificada", tags = "Tables", description = "A identificação é o número da mesa ou o UUID dela")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando a mesa não existe no banco de dados"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<Table> switchOccupied(@PathVariable String identification) {
        return ResponseEntity.ok(tableService.switchOccupied(identification));
    }

    @DeleteMapping("/waiter/{uuid}")
    @Operation(summary = "Deleta uma mesa", tags = "Tables")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quando a mesa não existe no banco de dados"),
            @ApiResponse(responseCode = "401", description = "Quando o usuário não está autenticado"),
            @ApiResponse(responseCode = "403", description = "Quando o usuário não possuí permissão"),
            @ApiResponse(responseCode = "500", description = "Quando acontece um erro no servidor")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        tableService.delete(uuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
