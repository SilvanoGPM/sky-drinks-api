package com.github.skyg0d.skydrinksapi;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.repository.user.ApplicationUserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDate;

@SpringBootApplication
public class SkyDrinksApiApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(SkyDrinksApiApplication.class, args);

        // Cria um usuário caso não exista nenhum.
        ApplicationUserRepository applicationUserRepository = ctx.getBean(ApplicationUserRepository.class);

        if (applicationUserRepository.count() == 0) {
            ApplicationUser applicationUser = ApplicationUser
                    .builder()
                    .name("Admin")
                    .email("admin@mail.com")
                    .password("$2a$10$BRlqrVhGnMHS.ZVi9D6VqeJsf0f2VzHJI16o6WHLR6LKckBAmcbOK")
                    .role("USER,BARMEN,WAITER,ADMIN")
                    .cpf("878.711.897-19")
                    .birthDay(LocalDate.of(2000, 4, 4))
                    .build();

            applicationUserRepository.save(applicationUser);
        }

    }

}
