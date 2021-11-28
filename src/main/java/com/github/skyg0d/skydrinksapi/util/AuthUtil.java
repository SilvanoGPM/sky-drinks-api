package com.github.skyg0d.skydrinksapi.util;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.exception.BadRequestException;
import com.github.skyg0d.skydrinksapi.repository.user.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final ApplicationUserRepository applicationUserRepository;

    public ApplicationUser getUser(Principal principal) {
        if (principal == null) {
            throw new BadRequestException("Aconteceu um erro ao tentar encontrar o usuário!");
        }

        ApplicationUser applicationUser = (ApplicationUser) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        return applicationUserRepository
                .findByEmail(applicationUser.getEmail())
                .orElseThrow(() -> new BadRequestException("Email do usuário não foi encontrado. . ."));
    }

}
