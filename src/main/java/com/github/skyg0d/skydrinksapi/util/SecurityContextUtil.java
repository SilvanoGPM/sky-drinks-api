package com.github.skyg0d.skydrinksapi.util;


import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class SecurityContextUtil {

    private SecurityContextUtil() {
    }

    public static void setSecurityContext(SignedJWT signedJWT) {
        try {
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            String email = claims.getSubject();

            if (email == null) {
                throw new JOSEException("Email está faltando no JWT");
            }

            if (!new Date().before(claims.getExpirationTime())) {
                throw new JOSEException("Token expirou!");
            }

            List<String> authorities = claims.getStringListClaim("authorities");

            ApplicationUser applicationUser = ApplicationUser
                    .builder()
                    .email(email)
                    .role(String.join(",", authorities))
                    .build();

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(applicationUser, null, createAuthorities((authorities)));

            auth.setDetails(signedJWT.serialize());

            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            log.error("Erro enquanto definia o security context", e);
            SecurityContextHolder.clearContext();
        }
    }

    private static List<SimpleGrantedAuthority> createAuthorities(List<String> authorities) {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
