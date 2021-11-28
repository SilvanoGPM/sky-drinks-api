package com.github.skyg0d.skydrinksapi.util.user;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ApplicationUserCreator {

    private static final UUID uuid = UUID.fromString("d3fd966f-0810-4263-a9bd-f6265d67cc4d");

    public static ApplicationUser createApplicationUserToBeSave() {
        return ApplicationUser
                .builder()
                .name("SkyG0D")
                .role("USER")
                .cpf("084.483.910-80")
                .email("skyg0d@mail.com")
                .password("$2a$10$2xF.jUnOGUlZOm8M763Tb.afhZ8/IiZqghD3z/Y2Rkzlv0B/W9kB")
                .birthDay(LocalDate.of(2000, 10, 9))
                .build();
    }

    public static ApplicationUser createValidApplicationUser() {
        return ApplicationUser
                .builder()
                .uuid(uuid)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .name("SkyG0D")
                .role("USER")
                .cpf("084.483.910-80")
                .email("skyg0d@mail.com")
                .password("$2a$10$2xF.jUnOGUlZOm8M763Tb.afhZ8/IiZqghD3z/Y2Rkzlv0B/W9kB")
                .birthDay(LocalDate.of(2000, 10, 9))
                .build();
    }

    public static ApplicationUser createUpdatedApplicationUser() {
        return ApplicationUser
                .builder()
                .uuid(uuid)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .name("Sky")
                .role("USER")
                .cpf("084.483.910-80")
                .email("sky@mail.com")
                .password("$2a$10$2xF.jUnOGUlZOm8M763Tb.afhZ8/IiZqghD3z/Y2Rkzlv0B/W9kB")
                .birthDay(LocalDate.of(2000, 10, 9))
                .build();
    }

}
