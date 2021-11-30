package com.github.skyg0d.skydrinksapi.util;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.repository.user.ApplicationUserRepository;
import lombok.*;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TokenUtil {

    @Autowired
    private final TestRestTemplate testRestTemplate;

    public TokenUtil(TestRestTemplate testRestTemplate, ApplicationUserRepository applicationUserRepository) {
        this.testRestTemplate = testRestTemplate;

        ApplicationUser adminUser = ApplicationUser
                .builder()
                .name("Admin")
                .role("ADMIN")
                .cpf("409.695.886-74")
                .email("admin@mail.com")
                .password("$2a$10$3eqv7nOc.CFIcOa7zpkwV.h/Jt4h0io6qAha8X/4zOeeexRi6afn2")
                .birthDay(LocalDate.of(2000, 10, 9))
                .build();

        ApplicationUser barmenUser = ApplicationUser
                .builder()
                .name("Barmen")
                .role("BARMEN")
                .cpf("457.411.373-18")
                .email("barmen@mail.com")
                .password("$2a$10$3eqv7nOc.CFIcOa7zpkwV.h/Jt4h0io6qAha8X/4zOeeexRi6afn2")
                .birthDay(LocalDate.of(2000, 10, 9))
                .build();

        ApplicationUser waiterUser = ApplicationUser
                .builder()
                .name("Waiter")
                .role("WAITER")
                .cpf("194.342.608-25")
                .email("waiter@mail.com")
                .password("$2a$10$3eqv7nOc.CFIcOa7zpkwV.h/Jt4h0io6qAha8X/4zOeeexRi6afn2")
                .birthDay(LocalDate.of(2000, 10, 9))
                .build();

        ApplicationUser user = ApplicationUser
                .builder()
                .name("User")
                .role("USER")
                .cpf("124.565.732-18")
                .email("user@mail.com")
                .password("$2a$10$3eqv7nOc.CFIcOa7zpkwV.h/Jt4h0io6qAha8X/4zOeeexRi6afn2")
                .birthDay(LocalDate.of(2000, 10, 9))
                .build();

        ApplicationUser minorUser = ApplicationUser
                .builder()
                .name("User Minor")
                .role("USER")
                .cpf("512.262.484-46")
                .email("userminor@mail.com")
                .password("$2a$10$3eqv7nOc.CFIcOa7zpkwV.h/Jt4h0io6qAha8X/4zOeeexRi6afn2")
                .birthDay(LocalDate.now())
                .build();

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
