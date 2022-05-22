package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.domain.*;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class ClientRequestService {

    private static final int MINORITY = 18;

    private final ClientRequestRepository clientRequestRepository;
    private final ApplicationUserService applicationUserService;
    private final DrinkService drinkService;
    private final ClientRequestMapper mapper = ClientRequestMapper.INSTANCE;

    private boolean blockAllRequests;

    public Page<ClientRequest> listAll(Pageable pageable) {
        log.info("Retornando todos os pedidos com os parametros \"{}\"", pageable);

        return clientRequestRepository.findAll(pageable);
    }

    public Page<ClientRequest> search(ClientRequestParameters parameters, Pageable pageable) {
        return clientRequestRepository.findAll(ClientRequestSpecification.getSpecification(parameters), pageable);
    }

    public Page<ClientRequest> searchMyRequests(ClientRequestParameters parameters, Pageable pageable, ApplicationUser user) {
        parameters.setUserUUID(user.getUuid());

        log.info("Pesquisando pedidos com as determinadas características \"{}\"", parameters);

        return clientRequestRepository.findAll(ClientRequestSpecification.getSpecification(parameters), pageable);
    }

    public ClientRequest findByIdOrElseThrowBadRequestException(UUID uuid) {
        log.info("Pesquisando pedido com uuid \"{}\"", uuid);

        return clientRequestRepository
                .findById(uuid)
                .orElseThrow(() -> new BadRequestException(String.format("Pedido com id %s não foi encontrado", uuid)));
    }

    public List<ClientRequestDrinkCount> getMyTopFiveDrinks(ApplicationUser user) {
        log.info("Retornando as cinco bebidas mais pedidas do usuário com uuid \"{}\"", user.getUuid());

        return clientRequestRepository.countTotalDrinksInRequest(user.getUuid(), PageRequest.of(0, 5));
    }

    public List<ClientRequestDrinkCount> getTopFiveDrinks(UUID uuid) {
        log.info("Retornando as cinco bebidas mais pedidas do usuário com uuid \"{}\"", uuid);

        return clientRequestRepository.countTotalDrinksInRequest(applicationUserService.findByIdOrElseThrowBadRequestException(uuid).getUuid(), PageRequest.of(0, 5));
    }

    public List<ClientRequestDrinkCount> getTopDrinksInRequests(Pageable pageable) {
        log.info("Retornando as bebidas mais pedidas de todos os tempos");

        return clientRequestRepository.countTotalDrinksInRequest(PageRequest.of(0, pageable.getPageSize()));
    }

    public List<ClientRequestDrinkCount> mostCanceledDrinks(Pageable pageable) {
        log.info("Retornando as bebidas mais canceladas de todos os tempos");

        return clientRequestRepository.mostCanceledDrinks(PageRequest.of(0, pageable.getPageSize()));
    }

    public List<ClientRequestAlcoholicDrinkCount> getTotalOfDrinksGroupedByAlcoholic(ApplicationUser user) {
        log.info("Retornando o total de bebidas do usuário com uuid \"{}\", agrupando pelo atributo de alcoolismo", user.getUuid());

        return clientRequestRepository.countAlcoholicDrinksInRequests(user.getUuid(), PageRequest.of(0, 2));
    }

    public List<ClientRequestDate> getAllDatesInRequests() {
        log.info("Retornando todas as datas dos pedidos");

        return clientRequestRepository.getAllDatesInRequests();
    }

    public ClientRequest save(ClientRequestPostRequestBody clientRequestPostRequestBody, ApplicationUser user) {
        log.info("Tentando criar usuário. . .");

        log.info("Verificando se todos os pedidos estão desbloqueados");

        if (blockAllRequests) {
            throw new BadRequestException("A criação de novos pedidos está bloqueada para todos os usuários!");
        }

        log.info("Verificando se o usuário com uuid \"{}\" está bloqueado", user.getUuid());

        if (user.isLockRequests()) {
            throw new UserRequestsAreLockedException("Usuário foi bloqueado temporariamente, logo não pode realizar novos pedidos", user.getLockRequestsTimestamp());
        }

        boolean containsAlcoholicDrink = clientRequestPostRequestBody.getDrinks().stream().anyMatch((drink) -> (
                drinkService.findByIdOrElseThrowBadRequestException(drink.getUuid()).isAlcoholic()
        ));

        long userAge = ChronoUnit.YEARS.between(user.getBirthDay(), LocalDateTime.now());

        log.info("Verificando se o usuário com uuid \"{}\" pediu bebidas alcoólicas e é menor de idade", user.getUuid());

        if (containsAlcoholicDrink && userAge < MINORITY) {
            throw new UserCannotCompleteClientRequestException("O usuário está tentando comprar bebidas alcoólicas, porém ele é menor de idade.", "Menor de idade");
        }

        ClientRequest request = mapper.toClientRequest(clientRequestPostRequestBody);

        double totalPrice = calculatePrice(request);

        request.setTotalPrice(totalPrice);
        request.setStatus(ClientRequestStatus.PROCESSING);
        request.setUser(user);

        log.info("Realizando pedido \"{}\", para o usuário com uuid \"{}\"", request, user.getUuid());

        return clientRequestRepository.save(request);
    }

    public void replace(ClientRequestPutRequestBody clientRequestPutRequestBody, ApplicationUser user) {
        ClientRequest request = findByIdOrElseThrowBadRequestException(clientRequestPutRequestBody.getUuid());

        userCanModifyRequestOrElseThrowUserCannotModifyClientRequestException(user, request);

        ClientRequest requestToUpdate = mapper.toClientRequest(clientRequestPutRequestBody);

        requestToUpdate.setTotalPrice(calculatePrice(requestToUpdate));
        requestToUpdate.setStatus(request.getStatus());
        requestToUpdate.setUser(request.getUser());

        log.info("Atualizando o pedido \"{}\"", request);

        clientRequestRepository.save(requestToUpdate);
    }

    public ClientRequest startRequest(UUID uuid) {
        log.info("Tentando iniciar o pedido com uuid \"{}\". . .", uuid);

        return setStatus(
                ClientRequestStatus.STARTED,
                uuid,
                null
        );
    }

    public ClientRequest finishRequest(UUID uuid) {
        log.info("Tentando finalizar o pedido com uuid \"{}\". . .", uuid);

        return setStatus(
                ClientRequestStatus.FINISHED,
                uuid,
                null
        );
    }

    public ClientRequest cancelRequest(UUID uuid, ApplicationUser user) {
        log.info("Tentando cancelar o pedido com uuid \"{}\". . .", uuid);

        return setStatus(
                ClientRequestStatus.CANCELED,
                uuid,
                user
        );
    }

    public ClientRequest deliverRequest(UUID uuid) {
        ClientRequest request = findByIdOrElseThrowBadRequestException(uuid);

        log.info("Tentando entregar o pedido com uuid \"{}\". . .", uuid);
        log.info("Verificando se o pedido com uuid \"{}\" não foi finalizado", uuid);

        if (!request.getStatus().equals(ClientRequestStatus.FINISHED)) {
            throw new BadRequestException("Não é possível entregar um pedido não finalizado!");
        }

        log.info("Verificando se o pedido com uuid \"{}\" já foi entregue", uuid);

        if (request.isDelivered()) {
            throw new BadRequestException(String.format("O pedido %s já foi entregue!", uuid));
        }

        log.info("Entregando pedido pedido com uuid \"{}\"", uuid);

        request.setDelivered(true);

        double totalPrice = calculatePrice(request);

        request.setTotalPrice(totalPrice);

        return clientRequestRepository.save(request);
    }

    public void delete(UUID uuid, ApplicationUser user) {
        ClientRequest request = findByIdOrElseThrowBadRequestException(uuid);

        userCanModifyRequestOrElseThrowUserCannotModifyClientRequestException(user, request);

        log.info("Deletando o pedido \"{}\"", request);

        clientRequestRepository.delete(request);
    }

    public boolean getAllBlocked() {
        log.info("Retornando se todos os pedidos estão bloqueados");

        return blockAllRequests;
    }

    public boolean toggleBlockAllRequests() {
        log.info("Invertendo o bloqueamento de todos os pedidos");

        blockAllRequests = !blockAllRequests;
        return blockAllRequests;
    }

    private ClientRequest setStatus(ClientRequestStatus status, UUID uuid, ApplicationUser user) {
        ClientRequest request = findByIdOrElseThrowBadRequestException(uuid);

        if (request.getStatus().equals(ClientRequestStatus.FINISHED)) {
            if (status.equals(ClientRequestStatus.CANCELED) && request.isDelivered()) {
                throw new BadRequestException("Um pedido já entregue não pode ser cancelado!");
            } else if (status.equals(ClientRequestStatus.FINISHED)) {
                throw new BadRequestException(String.format("Pedido com id %s já foi finalizado!", uuid));
            }
        } else if (request.getStatus().equals(ClientRequestStatus.CANCELED)) {
            String message = status.equals(ClientRequestStatus.FINISHED)
                    ? "Um pedido cancelado não pode ser finalizado!"
                    : String.format("Pedido com id %s já foi cancelado!", uuid);

            throw new BadRequestException(message);
        } else if (status.equals(ClientRequestStatus.STARTED) && !request.getStatus().equals(ClientRequestStatus.PROCESSING)) {
            throw new BadRequestException(String.format("Pedido com id %s precisa estar sendo processado para ser iniciado!", uuid));
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
        log.info("Verificando se o usuário com uuid \"{}\" pode modificar o pedido \"{}\"", user.getUuid(), request);

        if (!requestBelongsToUser(request, user) && !userIsStaff(user)) {
            String message = String.format("O usuário %s não possuí permissão suficiente para modificar o pedido de id: %s", user.getName(), request.getUuid());
            throw new UserCannotModifyClientRequestException(message, user, request);
        }
    }

    private boolean userIsStaff(ApplicationUser user) {
        log.info("Verificando se o usuário com uuid \"{}\" é um staff", user.getUuid());

        return !Roles.USER.getName().equals(user.getRole());
    }

    private boolean requestBelongsToUser(ClientRequest request, ApplicationUser user) {
        log.info("Verificando se o pedido \"{}\" pertence ao usuário com uuid \"{}\" é um staff", request, user.getUuid());

        return request.getUser().getUuid().equals(user.getUuid());
    }

    private double calculatePrice(ClientRequest request) {
        log.info("Calculando o preço do pedido \"{}\"", request);

        return request
                .getDrinks()
                .stream()
                .mapToDouble(Drink::getPrice)
                .sum();
    }

}
