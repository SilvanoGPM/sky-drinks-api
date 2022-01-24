package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.enums.Roles;
import com.github.skyg0d.skydrinksapi.exception.ActionNotAllowedException;
import com.github.skyg0d.skydrinksapi.exception.BadRequestException;
import com.github.skyg0d.skydrinksapi.exception.UserUniqueFieldExistsException;
import com.github.skyg0d.skydrinksapi.mapper.ApplicationUserMapper;
import com.github.skyg0d.skydrinksapi.parameters.ApplicationUserParameters;
import com.github.skyg0d.skydrinksapi.repository.request.ClientRequestRepository;
import com.github.skyg0d.skydrinksapi.repository.user.ApplicationUserRepository;
import com.github.skyg0d.skydrinksapi.repository.user.ApplicationUserSpecification;
import com.github.skyg0d.skydrinksapi.requests.ApplicationUserPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.ApplicationUserPutRequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class ApplicationUserService {

    private final ApplicationUserRepository applicationUserRepository;
    private final ClientRequestRepository clientRequestRepository;
    private final ApplicationUserMapper mapper = ApplicationUserMapper.INSTANCE;

    public Page<ApplicationUser> listAll(Pageable pageable) {
        log.info("Retornando todos os usuários com os parametros \"{}\"", pageable);

        return applicationUserRepository.findAll(pageable);
    }

    public Page<ApplicationUser> search(ApplicationUserParameters applicationUserParameters, Pageable pageable) {
        log.info("Pesquisando usuários com as determinadas características \"{}\"", applicationUserParameters);

        return applicationUserRepository.findAll(ApplicationUserSpecification.getSpecification(applicationUserParameters), pageable);
    }

    public ApplicationUser findByIdOrElseThrowBadRequestException(UUID uuid) {
        log.info("Pesquisando usuário com uuid \"{}\"", uuid);

        return applicationUserRepository
                .findById(uuid)
                .orElseThrow(() -> new BadRequestException(String.format("Usuário com id: \"%s\" não foi encontrado!", uuid)));
    }

    public ApplicationUser findByEmail(String email) {
        log.info("Pesquisando usuário com email \"{}\"", email);

        return applicationUserRepository
                .findByEmail(email)
                .orElseThrow(() -> new BadRequestException(String.format("Usuário com email: \"%s\" não foi encontrado!", email)));
    }

    public ApplicationUser findByCpf(String cpf) {
        log.info("Pesquisando usuário com cpf \"{}\"", cpf);

        return applicationUserRepository
                .findByCpf(cpf)
                .orElseThrow(() -> new BadRequestException(String.format("Usuário com cpf: \"%s\" não foi encontrado!", cpf)));
    }

    public List<ApplicationUser> getStaffUsers() {
        log.info("Pesquisando todos os mebros da staff");

        return applicationUserRepository.findAll(Specification.where(ApplicationUserSpecification.getStaffUsers()));
    }

    public ApplicationUser save(ApplicationUserPostRequestBody applicationUserPostRequestBody) {
        log.info("Tentando criar um usuário. . .");

        String email = applicationUserPostRequestBody.getEmail();

        Optional<ApplicationUser> userFound = applicationUserRepository.findByEmail(email);

        log.info("Verificando se o email \"{}\" já está sendo usado", email);

        if (userFound.isPresent()) {
            String message = String.format("Usuário com email: \"%s\" já existe", email);
            throw new UserUniqueFieldExistsException(message, "Email: " + email);
        }

        String cpf = applicationUserPostRequestBody.getCpf();

        Optional<ApplicationUser> cpfFound = applicationUserRepository.findByCpf(cpf);

        log.info("Verificando se o cpf \"{}\" já está sendo usado", cpf);

        if (cpfFound.isPresent()) {
            String message = String.format("Usuário com cpf: \"%s\" já existe", cpf);
            throw new UserUniqueFieldExistsException(message, "Cpf: " + cpf);
        }

        ApplicationUser userToCreate = mapper.toApplicationUser(applicationUserPostRequestBody);

        log.info("Criando o usuário, \"{}\"", userToCreate);

        return applicationUserRepository.save(userToCreate);
    }

    public void replace(ApplicationUserPutRequestBody applicationUserPutRequestBody, ApplicationUser user) {
        UUID uuid = applicationUserPutRequestBody.getUuid();

        log.info("Tentando atualizar usuário com uuid \"{}\". . .", uuid);

        verifyIfUserHasPermission(uuid, user);

        ApplicationUser userFound = findByIdOrElseThrowBadRequestException(uuid);
        ApplicationUser userMapped = mapper.toApplicationUser(applicationUserPutRequestBody);

        if (userMapped.getPassword() == null || userMapped.getPassword().isEmpty()) {
            userMapped.setPassword(userFound.getPassword());
        }

        log.info("Atualizando usuário com uuid \"{}\"", uuid);

        applicationUserRepository.save(userMapped);
    }

    public ApplicationUser toggleLockRequests(UUID uuid) {
        ApplicationUser userFound = findByIdOrElseThrowBadRequestException(uuid);

        log.info("Invertendo o bloqueamento de pedidos do usuário com uuid \"{}\"", uuid);

        boolean isUserRequestsLockedNow = !userFound.isLockRequests();
        LocalDateTime lockedTimestamp = isUserRequestsLockedNow ? LocalDateTime.now() : null;

        userFound.setLockRequests(isUserRequestsLockedNow);
        userFound.setLockRequestsTimestamp(lockedTimestamp);

        return applicationUserRepository.save(userFound);
    }

    public void delete(UUID uuid, ApplicationUser user) {
        verifyIfUserHasPermission(uuid, user);

        log.info("Deletando usuário com uuid \"{}\"", uuid);

        ApplicationUser userFound = findByIdOrElseThrowBadRequestException(uuid);

        Set<ClientRequest> requests = userFound.getRequests();

        if (requests != null && !requests.isEmpty()) {
            log.info("Deletando todos os pedidos do usuário");

            clientRequestRepository.deleteAll(requests);
        }

        applicationUserRepository.delete(userFound);
    }

    private void verifyIfUserHasPermission(UUID uuid, ApplicationUser user) {
        log.info("Verificando se usuário possui permissão");

        if (!user.getRole().contains(Roles.ADMIN.getName()) && !user.getUuid().equals(uuid)) {
            throw new ActionNotAllowedException("Apenas o usuário original ou admins podem alterar dados.");
        }

        log.info("Usuário possui permissão");
    }

}
