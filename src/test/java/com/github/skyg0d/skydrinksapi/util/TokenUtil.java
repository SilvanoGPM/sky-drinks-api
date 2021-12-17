package com.github.skyg0d.skydrinksapi.util;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.repository.user.ApplicationUserRepository;
import com.github.skyg0d.skydrinksapi.util.user.ApplicationUserCreator;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TokenUtil {

    @Autowired
    private final TestRestTemplate testRestTemplate;

    public TokenUtil(TestRestTemplate testRestTemplate, ApplicationUserRepository applicationUserRepository) {
        this.testRestTemplate = testRestTemplate;

        ApplicationUser adminUser = ApplicationUserCreator.createAdminApplicationUser();

        ApplicationUser barmenUser = ApplicationUserCreator.createBarmenApplicationUser();

        ApplicationUser waiterUser = ApplicationUserCreator.createWaiterApplicationUser();

        ApplicationUser user = ApplicationUserCreator.createApplicationUser();

        ApplicationUser minorUser = ApplicationUserCreator.createMinorApplicationUser();

        applicationUserRepository.saveAll(List.of(adminUser, barmenUser, waiterUser, user, minorUser));
    }

    public <T> HttpEntity<T> createAdminAuthEntity(T t) {
        return createAuthEntity(t, new Login("admin@mail.com", "admin"));
    }

    public <T> HttpEntity<T> createBarmenAuthEntity(T t) {
        return createAuthEntity(t, new Login("barmen@mail.com", "admin"));
    }

    public <T> HttpEntity<T> createWaiterAuthEntity(T t) {
        return createAuthEntity(t, new Login("waiter@mail.com", "admin"));
    }

    public <T> HttpEntity<T> createUserAuthEntity(T t) {
        return createAuthEntity(t, new Login("user@mail.com", "admin"));
    }

    public <T> HttpEntity<T> createUserMinorAuthEntity(T t) {
        return createAuthEntity(t, new Login("userminor@mail.com", "admin"));
    }

    public <T> HttpEntity<T> createAuthEntity(T t, Login login) {
        ResponseEntity<String> entity = testRestTemplate.postForEntity("/login", new HttpEntity<>(login), String.class);

        List<String> authorization = entity.getHeaders().get("Authorization");

        if (authorization == null) {
            throw new RuntimeException("Não foi possível adquirir o token.");
        }

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(authorization.get(0));

        return new HttpEntity<>(t, headers);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    private static class Login {

        private String email;
        private String password;

    }

}
