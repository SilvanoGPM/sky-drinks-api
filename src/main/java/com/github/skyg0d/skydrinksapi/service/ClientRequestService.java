package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.exception.BadRequestException;
import com.github.skyg0d.skydrinksapi.mapper.ClientRequestMapper;
import com.github.skyg0d.skydrinksapi.parameters.ClientRequestParameters;
import com.github.skyg0d.skydrinksapi.repository.request.ClientRequestRepository;
import com.github.skyg0d.skydrinksapi.repository.request.ClientRequestSpecification;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPutRequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientRequestService {

    private final ClientRequestRepository clientRequestRepository;
    private final ClientRequestMapper mapper = ClientRequestMapper.INSTANCE;

    public Page<ClientRequest> list(Pageable pageable) {
        return clientRequestRepository.findAll(pageable);
    }

    public Page<ClientRequest> search(ClientRequestParameters parameters, Pageable pageable) {
        return clientRequestRepository.findAll(ClientRequestSpecification.getSpecification(parameters), pageable);
    }

    public ClientRequest findByIdOrElseThrowBadRequestException(UUID uuid) {
        return clientRequestRepository
                .findById(uuid)
                .orElseThrow(() -> new BadRequestException(String.format("Pedido com id %s não foi encontrado", uuid)));
    }

    public ClientRequest save(ClientRequestPostRequestBody clientRequestPostRequestBody) {
        return clientRequestRepository.save(mapper.toClientRequest(clientRequestPostRequestBody));
    }

    public void replace(ClientRequestPutRequestBody clientRequestPutRequestBody) {
        findByIdOrElseThrowBadRequestException(clientRequestPutRequestBody.getUuid());
        clientRequestRepository.save(mapper.toClientRequest(clientRequestPutRequestBody));
    }

    public ClientRequest finishRequest(UUID uuid) {
        ClientRequest request = findByIdOrElseThrowBadRequestException(uuid);

        if (request.isFinished()) {
            throw new BadRequestException(String.format("Pedido com id %s já foi finalizado!", uuid));
        }

        request.setFinished(true);

        double totalPrice = request
                .getDrinks()
                .stream()
                .mapToDouble(Drink::getPrice)
                .sum();

        request.setTotalPrice(totalPrice);

        return clientRequestRepository.save(request);
    }

    public void delete(UUID uuid) {
        clientRequestRepository.delete(findByIdOrElseThrowBadRequestException(uuid));
    }

}
