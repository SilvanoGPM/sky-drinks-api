package com.github.skyg0d.skydrinksapi.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Log4j2
public class PasswordGenerator {

    public static void main(String[] args) {
        String toEnconde = args.length > 0 ? args[0] : "admin";

        String password = new BCryptPasswordEncoder().encode(toEnconde);

        log.info(password);
    }

}
