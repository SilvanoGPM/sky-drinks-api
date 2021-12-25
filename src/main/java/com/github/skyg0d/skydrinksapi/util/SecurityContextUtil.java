package com.github.skyg0d.skydrinksapi.util;


import com.github.skyg0d.skydrinksapi.exception.TokenExpiredException;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@Log4j2
public class SecurityContextUtil {

    private SecurityContextUtil() {
    }

    public static void setSecurityContext(SignedJWT signedJWT) {
        try {
            UsernamePasswordAuthenticationToken auth = PrincipalCreatorUtil.createPrincipal(signedJWT);

            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            log.error("Erro enquanto definia o security context: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            throw new TokenExpiredException(e.getMessage());
        }
    }

}
