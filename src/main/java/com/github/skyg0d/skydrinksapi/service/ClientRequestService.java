package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.enums.ClientRequestStatus;
import com.github.skyg0d.skydrinksapi.enums.Roles;
import com.github.skyg0d.skydrinksapi.exception.BadRequestException;
import com.github.skyg0d.skydrinksapi.exception.UserCannotCompleteClientRequestException;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ClientRequestService {

    private static final int MINORITY = 18;

    private final ClientRequestRepository clientRequestRepository;
    private final DrinkService drinkService;
    private final ClientRequestMapper mapper = ClientRequestMapper.INSTANCE;

    public Page<ClientRequest> listAll(Pageable pageable) {
        return clientRequestRepository.findAll(pageable);
    }

    public Page<ClientRequest> search(ClientRequestParameters parameters, Pageable pageable, ApplicationUser user) {
        Page<ClientRequest> requests = clientRequestRepository.findAll(ClientRequestSpecification.getSpecification(parameters), pageable);

        return userIsStaff(user)
                ? requests
                : filterRequestsByUser(requests, user, pageable);
    }

    public ClientRequest findByIdOrElseThrowBadRequestException(UUID uuid) {
        return clientRequestRepository
                .findById(uuid)
                .orElseThrow(() -> new BadRequestException(String.format("Pedido com id %s não foi encontrado", uuid)));
    }

    public ClientRequest save(ClientRequestPostRequestBody clientRequestPostRequestBody, ApplicationUser user) {
        boolean containsAlcoholicDrink = clientRequestPostRequestBody.getDrinks().stream().anyMatch((drink) -> (
                drinkService.findByIdOrElseThrowBadRequestException(drink.getUuid()).isAlcoholic()
        ));

        long userAge = ChronoUnit.YEARS.between(user.getBirthDay(), LocalDateTime.now());

        if (containsAlcoholicDrink && userAge < MINORITY) {
            throw new UserCannotCompleteClientRequestException("O usuário está tentando comprar bebidas alcoólicas, porém ele é menor de idade.", "Menor de idade");
        }

        ClientRequest request = mapper.toClientRequest(clientRequestPostRequestBody);

        double totalPrice = calculatePrice(request);

        request.setTotalPrice(totalPrice);
        request.setStatus(ClientRequestStatus.PROCESSING);
        request.setUser(user);

        return clientRequestRepository.save(request);
    }

    public void replace(ClientRequestPutRequestBody clientRequestPutRequestBody, ApplicationUser user) {
        ClientRequest request = findByIdOrElseThrowBadRequestException(clientRequestPutRequestBody.getUuid());

        userCanModifyRequestOrElseThrowUserCannotModifyClientRequestException(user, request);

        ClientRequest requestToUpdate = mapper.toClientRequest(clientRequestPutRequestBody);

        requestToUpdate.setTotalPrice(calculatePrice(requestToUpdate));
        requestToUpdate.setStatus(request.getStatus());
        requestToUpdate.setUser(request.getUser());

        clientRequestRepository.save(requestToUpdate);
    }

    public ClientRequest finishRequest(UUID uuid, ApplicationUser user) {
        return setStatus(
                ClientRequestStatus.FINISHED,
                String.format("Pedido com id %s já foi finalizado!", uuid),
                uuid,
                user
        );
    }

    public ClientRequest cancelRequest(UUID uuid, ApplicationUser user) {
        return setStatus(
                ClientRequestStatus.CANCELED,
                String.format("Pedido com id %s já foi cancelado!", uuid),
                uuid,
                user
        );
    }

    public void delete(UUID uuid, ApplicationUser user) {
        ClientRequest request = findByIdOrElseThrowBadRequestException(uuid);

        userCanModifyRequestOrElseThrowUserCannotModifyClientRequestException(user, request);

        clientRequestRepository.delete(request);
    }

    private ClientRequest setStatus(ClientRequestStatus status, String errorMessage, UUID uuid, ApplicationUser user) {
        ClientRequest request = findByIdOrElseThrowBadRequestException(uuid);

        if (request.getStatus().equals(ClientRequestStatus.FINISHED)) {
            String message = status.equals(ClientRequestStatus.CANCELED)
                    ? "Um pedido finalizado não pode ser cancelado!"
                    : String.format("Pedido com id %s já foi finalizado!", uuid);

            throw new BadRequestException(message);
        } else if (request.getStatus().equals(ClientRequestStatus.CANCELED)) {
            String message = status.equals(ClientRequestStatus.FINISHED)
                    ? "Um pedido cancelado não pode ser finalizado!"
                    : String.format("Pedido com id %s já foi cancelado!", uuid);

            throw new BadRequestException(message);
        }

        userCanModifyRequestOrElseThrowUserCannotModifyClientRequestException(user, request);

        request.setStatus(status);

        double totalPrice = calculatePrice(request);

        request.setTotalPrice(totalPrice);

        return clientRequestRepository.save(request);
    }

    private void userCanModifyRequestOrElseThrowUserCannotModifyClientRequestException(ApplicationUser user, ClientRequest request) {
        if (!requestBelongsToUser(request, user) && !userIsStaff(user)) {
            String message = String.format("O usuário %s não possuí permissão suficiente para modificar o pedido de id: %s", user.getName(), request.getUuid());
            throw new UserCannotModifyClientRequestException(message, user, request);
        }
    }

    private boolean userIsStaff(ApplicationUser user) {
        return !user.getRole().contains(Roles.USER.getName());
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

    private double calculatePrice(ClientRequest request) {
        return request
                .getDrinks()
                .stream()
                .mapToDouble(Drink::getPrice)
                .sum();
    }

}
