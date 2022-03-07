package com.github.skyg0d.skydrinksapi.util;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.enums.Roles;
import com.github.skyg0d.skydrinksapi.exception.ActionNotAllowedException;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

@Log4j2
public class RolesUtil {

    public static void verifyIfUserHasPermission(UUID uuid, ApplicationUser user) {
        log.info("Verificando se usuário possui permissão");

        if (!user.getRole().contains(Roles.ADMIN.getName()) && !user.getUuid().equals(uuid)) {
            throw new ActionNotAllowedException("Apenas o usuário original ou admins podem alterar dados.");
        }

        log.info("Usuário possui permissão");
    }

}
