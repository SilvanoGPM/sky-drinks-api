package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.exception.BadRequestException;
import com.github.skyg0d.skydrinksapi.exception.UserUniqueFieldExistsException;
import com.github.skyg0d.skydrinksapi.mapper.ApplicationUserMapper;
import com.github.skyg0d.skydrinksapi.parameters.ApplicationUserParameters;
import com.github.skyg0d.skydrinksapi.repository.user.ApplicationUserRepository;
import com.github.skyg0d.skydrinksapi.repository.user.ApplicationUserSpecification;
import com.github.skyg0d.skydrinksapi.requests.ApplicationUserPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.ApplicationUserPutRequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationUserService {

    private final ApplicationUserRepository applicationUserRepository;
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

    public ApplicationUser save(ApplicationUserPostRequestBody applicationUserPostRequestBody) {
        String email = applicationUserPostRequestBody.getEmail();

        Optional<ApplicationUser> emailFound = applicationUserRepository.findByEmail(email);

        if (emailFound.isPresent()) {
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

    public void replace(ApplicationUserPutRequestBody applicationUserPutRequestBody) {
        findByIdOrElseThrowBadRequestException(applicationUserPutRequestBody.getUuid());
        applicationUserRepository.save(mapper.toApplicationUser(applicationUserPutRequestBody));
    }

    public void delete(UUID uuid) {
        applicationUserRepository.delete(findByIdOrElseThrowBadRequestException(uuid));
    }

}
