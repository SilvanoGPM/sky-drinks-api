package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.enums.Roles;
import com.github.skyg0d.skydrinksapi.exception.BadRequestException;
import com.github.skyg0d.skydrinksapi.exception.UserCannotModifyClientRequestException;
import com.github.skyg0d.skydrinksapi.mapper.ClientRequestMapper;
import com.github.skyg0d.skydrinksapi.parameters.ClientRequestParameters;
import com.github.skyg0d.skydrinksapi.repository.request.ClientRequestRepository;
import com.github.skyg0d.skydrinksapi.repository.request.ClientRequestSpecification;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPutRequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientRequestService {

    private final ClientRequestRepository clientRequestRepository;
    private final ClientRequestMapper mapper = ClientRequestMapper.INSTANCE;

    public Page<ClientRequest> listAll(Pageable pageable) {
        return clientRequestRepository.findAll(pageable);
    }

    public Page<ClientRequest> search(ClientRequestParameters parameters, Pageable pageable, ApplicationUser user) {
        Page<ClientRequest> requests = clientRequestRepository.findAll(ClientRequestSpecification.getSpecification(parameters), pageable);

        return userIsWaiter(user)
                ? requests
                : filterRequestsByUser(requests, user, pageable);
    }

    public ClientRequest findByIdOrElseThrowBadRequestException(UUID uuid) {
        return clientRequestRepository
                .findById(uuid)
                .orElseThrow(() -> new BadRequestException(String.format("Pedido com id %s não foi encontrado", uuid)));
    }

    public ClientRequest save(ClientRequestPostRequestBody clientRequestPostRequestBody, ApplicationUser user) {
        ClientRequest request = mapper.toClientRequest(clientRequestPostRequestBody);

        request.setUser(user);

        return clientRequestRepository.save(request);
    }

    public void replace(ClientRequestPutRequestBody clientRequestPutRequestBody, ApplicationUser user) {
        ClientRequest request = findByIdOrElseThrowBadRequestException(clientRequestPutRequestBody.getUuid());

        userCanModifyRequestOrElseThrow(user, request);

        clientRequestRepository.save(mapper.toClientRequest(clientRequestPutRequestBody));
    }

    public ClientRequest finishRequest(UUID uuid, ApplicationUser user) {
        ClientRequest request = findByIdOrElseThrowBadRequestException(uuid);

        if (request.isFinished()) {
            throw new BadRequestException(String.format("Pedido com id %s já foi finalizado!", uuid));
        }

        userCanModifyRequestOrElseThrow(user, request);

        request.setFinished(true);

        double totalPrice = request
                .getDrinks()
                .stream()
                .mapToDouble(Drink::getPrice)
                .sum();

        request.setTotalPrice(totalPrice);

        return clientRequestRepository.save(request);
    }

    public void delete(UUID uuid, ApplicationUser user) {
        ClientRequest request = findByIdOrElseThrowBadRequestException(uuid);

        userCanModifyRequestOrElseThrow(user, request);

        clientRequestRepository.delete(request);
    }

    private void userCanModifyRequestOrElseThrow(ApplicationUser user, ClientRequest request) {
        if (!requestBelongsToUser(request, user) && !userIsWaiter(user)) {
            String message = String.format("O usuário %s não possuí permissão suficiente para modificar o pedido de id: %s", user.getName(), request.getUuid());
            throw new UserCannotModifyClientRequestException(message, user, request);
        }
    }

    private boolean userIsWaiter(ApplicationUser user) {
        return user.getRole().contains(Roles.WAITER.getName());
    }

    private boolean requestBelongsToUser(ClientRequest request, ApplicationUser user) {
        return request.getUser().getUuid().equals(user.getUuid());
    }

    private Page<ClientRequest> filterRequestsByUser(Page<ClientRequest> requests, ApplicationUser user, Pageable pageable) {
        List<ClientRequest> requestsFiltered = requests
                .stream()
                .filter(((request) -> requestBelongsToUser(request, user)))
                .collect(Collectors.toList());

        return new PageImpl<>(requestsFiltered, pageable, requestsFiltered.size());
    }

}
