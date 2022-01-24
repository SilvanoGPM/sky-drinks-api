package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.domain.Table;
import com.github.skyg0d.skydrinksapi.exception.BadRequestException;
import com.github.skyg0d.skydrinksapi.mapper.TableMapper;
import com.github.skyg0d.skydrinksapi.parameters.TableParameters;
import com.github.skyg0d.skydrinksapi.repository.request.ClientRequestRepository;
import com.github.skyg0d.skydrinksapi.repository.table.TableRepository;
import com.github.skyg0d.skydrinksapi.repository.table.TableSpecification;
import com.github.skyg0d.skydrinksapi.requests.TablePostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.TablePutRequestBody;
import com.github.skyg0d.skydrinksapi.util.UUIDUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Log4j2
public class TableService {

    private final TableRepository tableRepository;
    private final ClientRequestRepository clientRequestRepository;
    private final TableMapper mapper = TableMapper.INSTANCE;
    private final UUIDUtil uuidUtil;

    public Page<Table> listAll(Pageable pageable) {
        log.info("Retornando todos as tabelas com os parametros \"{}\"", pageable);

        return tableRepository.findAll(pageable);
    }

    public Page<Table> search(TableParameters tableParameters, Pageable pageable) {
        log.info("Pesquisando mesas com as determinadas características \"{}\"", tableParameters);

        return tableRepository.findAll(TableSpecification.getSpecification(tableParameters), pageable);
    }

    public Table findByIdOrElseThrowBadRequestException(UUID uuid) {
        log.info("Pesquisando mesa com uuid \"{}\"", uuid);

        return tableRepository
                .findById(uuid)
                .orElseThrow(() -> new BadRequestException(String.format("Mesa com id %s não foi encontrada.", uuid)));
    }

    public Table findByNumberOrElseThrowBadRequestException(int number) {
        log.info("Pesquisando mesa com número \"{}\"", number);

        return tableRepository
                .findByNumber(number)
                .orElseThrow(() -> new BadRequestException(String.format("Mesa com número %d não foi encontrada.", number)));
    }

    public Table save(TablePostRequestBody tablePostRequestBody) {
        log.info("Tentando criar mesa. . .");

        int tableNumber = tablePostRequestBody.getNumber();
        Optional<Table> tableFound = tableRepository.findByNumber(tableNumber);

        log.info("Verificando se a mesa com número \"{}\" já existe", tableNumber);

        if (tableFound.isPresent()) {
            throw new BadRequestException(String.format("Mesa com número %d já existe!", tableNumber));
        }

        log.info("Criando mesa com número \"{}\"", tableNumber);

        return tableRepository.save(mapper.toTable(tablePostRequestBody));
    }

    public void replace(TablePutRequestBody tablePutRequestBody) {
        findByIdOrElseThrowBadRequestException(tablePutRequestBody.getUuid());

        log.info("Atualizando mesa com uuid \"{}\"", tablePutRequestBody.getUuid());

        tableRepository.save(mapper.toTable(tablePutRequestBody));
    }

    public Table switchOccupied(String identification) {
        UUID uuid = uuidUtil.getUUID(identification);

        Table foundTable = uuid != null
                ? findByIdOrElseThrowBadRequestException(uuid)
                : findByNumberOrElseThrowBadRequestException(Integer.parseInt(identification));

        log.info("Invertendo a ocupação da mesa com identificação \"{}\"", identification);

        foundTable.setOccupied(!foundTable.isOccupied());

        return tableRepository.save(foundTable);
    }

    public void delete(UUID uuid) {
        Table tableFound = findByIdOrElseThrowBadRequestException(uuid);

        Set<ClientRequest> requests = tableFound.getRequests();

        log.info("Deletando mesa com uuid \"{}\"", uuid);

        if (requests != null && !requests.isEmpty()) {
            log.info("Removendo mesa dos pedidos");

            for (ClientRequest request : requests) {
                Table table = request.getTable();

                if (tableFound.equals(table)) {
                    request.setTable(null);
                    clientRequestRepository.save(request);
                }
            }
        }

        tableRepository.delete(tableFound);
    }

}
