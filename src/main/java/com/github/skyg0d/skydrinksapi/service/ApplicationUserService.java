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
        return applicationUserRepository.findAll(pageable);
    }

    public Page<ApplicationUser> search(ApplicationUserParameters applicationUserParameters, Pageable pageable) {
        return applicationUserRepository.findAll(ApplicationUserSpecification.getSpecification(applicationUserParameters), pageable);
    }

    public ApplicationUser findByIdOrElseThrowBadRequestException(UUID uuid) {
        return applicationUserRepository
                .findById(uuid)
                .orElseThrow(() -> new BadRequestException(String.format("Usuário com id: \"%s\" não foi encontrado!", uuid)));
    }

    public ApplicationUser findByEmail(String email) {
        return applicationUserRepository
                .findByEmail(email)
                .orElseThrow(() -> new BadRequestException(String.format("Usuário com email: \"%s\" não foi encontrado!", email)));
    }

    public ApplicationUser findByCpf(String cpf) {
        return applicationUserRepository
                .findByCpf(cpf)
                .orElseThrow(() -> new BadRequestException(String.format("Usuário com cpf: \"%s\" não foi encontrado!", cpf)));
    }

    public List<ApplicationUser> getStaffUsers() {
        return applicationUserRepository.findAll(Specification.where(ApplicationUserSpecification.getStaffUsers()));
    }

    public ApplicationUser save(ApplicationUserPostRequestBody applicationUserPostRequestBody) {
        String email = applicationUserPostRequestBody.getEmail();

        Optional<ApplicationUser> userFound = applicationUserRepository.findByEmail(email);

        if (userFound.isPresent()) {
            String message = String.format("Usuário com email: \"%s\" já existe", email);
            throw new UserUniqueFieldExistsException(message, "Email: " + email);
        }

        String cpf = applicationUserPostRequestBody.getCpf();

        Optional<ApplicationUser> cpfFound = applicationUserRepository.findByCpf(cpf);

        if (cpfFound.isPresent()) {
            String message = String.format("Usuário com cpf: \"%s\" já existe", cpf);
            throw new UserUniqueFieldExistsException(message, "Cpf: " + cpf);
        }

        return applicationUserRepository.save(mapper.toApplicationUser(applicationUserPostRequestBody));
    }

    public void replace(ApplicationUserPutRequestBody applicationUserPutRequestBody, ApplicationUser user) {
        UUID uuid = applicationUserPutRequestBody.getUuid();

        verifyIfUserHasPermission(uuid, user);

        ApplicationUser userFound = findByIdOrElseThrowBadRequestException(uuid);
        ApplicationUser userMapped = mapper.toApplicationUser(applicationUserPutRequestBody);

        if (userMapped.getPassword() == null || userMapped.getPassword().isEmpty()) {
            userMapped.setPassword(userFound.getPassword());
        }

        applicationUserRepository.save(userMapped);
    }

    public ApplicationUser toggleLockRequests(UUID uuid) {
        ApplicationUser userFound = findByIdOrElseThrowBadRequestException(uuid);

        boolean isUserRequestsLockedNow = !userFound.isLockRequests();
        LocalDateTime lockedTimestamp = isUserRequestsLockedNow ? LocalDateTime.now() : null;

        userFound.setLockRequests(isUserRequestsLockedNow);
        userFound.setLockRequestsTimestamp(lockedTimestamp);

        return applicationUserRepository.save(userFound);
    }

    public void delete(UUID uuid, ApplicationUser user) {
        verifyIfUserHasPermission(uuid, user);

        ApplicationUser userFound = findByIdOrElseThrowBadRequestException(uuid);

        Set<ClientRequest> requests = userFound.getRequests();

        if (requests != null && !requests.isEmpty()) {
            clientRequestRepository.deleteAll(requests);
        }

        applicationUserRepository.delete(userFound);
    }

    private void verifyIfUserHasPermission(UUID uuid, ApplicationUser user) {
        if (!user.getRole().contains(Roles.ADMIN.getName()) && !user.getUuid().equals(uuid)) {
            throw new ActionNotAllowedException("Apenas o usuário original ou admins podem alterar dados.");
        }
    }

}
