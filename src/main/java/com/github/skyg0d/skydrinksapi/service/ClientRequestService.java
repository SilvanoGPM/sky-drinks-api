package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.domain.Drink;
import com.github.skyg0d.skydrinksapi.enums.ClientRequestStatus;
import com.github.skyg0d.skydrinksapi.enums.Roles;
import com.github.skyg0d.skydrinksapi.exception.BadRequestException;
import com.github.skyg0d.skydrinksapi.exception.UserCannotCompleteClientRequestException;
import com.github.skyg0d.skydrinksapi.exception.UserCannotModifyClientRequestException;
import com.github.skyg0d.skydrinksapi.exception.UserRequestsAreLockedException;
import com.github.skyg0d.skydrinksapi.mapper.ClientRequestMapper;
import com.github.skyg0d.skydrinksapi.parameters.ClientRequestParameters;
import com.github.skyg0d.skydrinksapi.repository.request.ClientRequestRepository;
import com.github.skyg0d.skydrinksapi.repository.request.ClientRequestSpecification;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.ClientRequestPutRequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class ClientRequestService {

    private static final int MINORITY = 18;

    private final ClientRequestRepository clientRequestRepository;
    private final DrinkService drinkService;
    private final ClientRequestMapper mapper = ClientRequestMapper.INSTANCE;

    private boolean blockAllRequests;

    public Page<ClientRequest> listAll(Pageable pageable) {
        return clientRequestRepository.findAll(pageable);
    }

    public Page<ClientRequest> search(ClientRequestParameters parameters, Pageable pageable) {
        return clientRequestRepository.findAll(ClientRequestSpecification.getSpecification(parameters), pageable);
    }

    public Page<ClientRequest> searchMyRequests(ClientRequestParameters parameters, Pageable pageable, ApplicationUser user) {
        parameters.setUserUUID(user.getUuid());
        return clientRequestRepository.findAll(ClientRequestSpecification.getSpecification(parameters), pageable);
    }

    public ClientRequest findByIdOrElseThrowBadRequestException(UUID uuid) {
        return clientRequestRepository
                .findById(uuid)
                .orElseThrow(() -> new BadRequestException(String.format("Pedido com id %s não foi encontrado", uuid)));
    }

    public ClientRequest save(ClientRequestPostRequestBody clientRequestPostRequestBody, ApplicationUser user) {
        if (blockAllRequests) {
            throw new BadRequestException("A criação de novos pedidos está bloqueada para todos os usuários!");
        }

        if (user.isLockRequests()) {
            throw new UserRequestsAreLockedException("Usuário foi bloqueado temporariamente, logo não pode realizar novos pedidos", user.getLockRequestsTimestamp());
        }

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

    public ClientRequest finishRequest(UUID uuid) {
        return setStatus(
                ClientRequestStatus.FINISHED,
                uuid,
                null
        );
    }

    public ClientRequest cancelRequest(UUID uuid, ApplicationUser user) {
        return setStatus(
                ClientRequestStatus.CANCELED,
                uuid,
                user
        );
    }

    public ClientRequest deliverRequest(UUID uuid) {
        ClientRequest request = findByIdOrElseThrowBadRequestException(uuid);

        if (!request.getStatus().equals(ClientRequestStatus.FINISHED)) {
            throw new BadRequestException("Não é possível entregar um pedido não finalizado!");
        }

        if (request.isDelivered()) {
            throw new BadRequestException(String.format("O pedido %s já foi entregue!", uuid));
        }

        request.setDelivered(true);

        double totalPrice = calculatePrice(request);

        request.setTotalPrice(totalPrice);

        return clientRequestRepository.save(request);
    }

    public void delete(UUID uuid, ApplicationUser user) {
        ClientRequest request = findByIdOrElseThrowBadRequestException(uuid);

        userCanModifyRequestOrElseThrowUserCannotModifyClientRequestException(user, request);

        clientRequestRepository.delete(request);
    }

    public boolean getAllBlocked() {
        return blockAllRequests;
    }

    public boolean toggleBlockAllRequests() {
        blockAllRequests = !blockAllRequests;
        return blockAllRequests;
    }

    private ClientRequest setStatus(ClientRequestStatus status, UUID uuid, ApplicationUser user) {
        ClientRequest request = findByIdOrElseThrowBadRequestException(uuid);

        if (request.getStatus().equals(ClientRequestStatus.FINISHED)) {
            String message = status.equals(ClientRequestStatus.CANCELED) && request.isDelivered()
                    ? "Um pedido já entregue não pode ser cancelado!"
                    : String.format("Pedido com id %s já foi finalizado!", uuid);

            throw new BadRequestException(message);
        } else if (request.getStatus().equals(ClientRequestStatus.CANCELED)) {
            String message = status.equals(ClientRequestStatus.FINISHED)
                    ? "Um pedido cancelado não pode ser finalizado!"
                    : String.format("Pedido com id %s já foi cancelado!", uuid);

            throw new BadRequestException(message);
        }

        if (user != null) {
            userCanModifyRequestOrElseThrowUserCannotModifyClientRequestException(user, request);
        }

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
        return !Roles.USER.getName().equals(user.getRole());
    }

    private boolean requestBelongsToUser(ClientRequest request, ApplicationUser user) {
        return request.getUser().getUuid().equals(user.getUuid());
    }

    private double calculatePrice(ClientRequest request) {
        return request
                .getDrinks()
                .stream()
                .mapToDouble(Drink::getPrice)
                .sum();
    }

}
