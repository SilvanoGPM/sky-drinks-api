package com.github.skyg0d.skydrinksapi.util;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.repository.user.ApplicationUserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
@Log4j2
public class LoadDatabase {

    @Bean
    CommandLineRunner createUserIfNoneExists(ApplicationUserRepository applicationUserRepository) {
        return (args) -> {
            log.info("Verificando se existe algum usuário. . .");

            if (applicationUserRepository.count() == 0) {
                log.info("Nenhum usuário encontrado, criando usuário padrão.");

                ApplicationUser applicationUser = ApplicationUser
                        .builder()
                        .name("Admin")
                        .email("admin@mail.com")
                        .password("$2a$10$RF0S4Bpk7YB487xhjObLPuEGdg7cV/U6mEyhHqble1H.IApmTqf6m")
                        .role("USER,BARMEN,WAITER,ADMIN")
                        .cpf("878.711.897-19")
                        .birthDay(LocalDate.of(2000, 4, 4))
                        .build();

                applicationUserRepository.save(applicationUser);

                log.info("Usuário padrão criado: {}", applicationUser);
            }
        };
    }

}
