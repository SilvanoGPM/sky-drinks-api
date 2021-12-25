package com.github.skyg0d.skydrinksapi.util;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.exception.TokenExpiredException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PrincipalCreatorUtil {

    public static UsernamePasswordAuthenticationToken createPrincipal(SignedJWT signedJWT) throws JOSEException, ParseException {
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        String email = claims.getSubject();

        if (email == null) {
            throw new JOSEException("Email está faltando no JWT");
        }

        if (!new Date().before(claims.getExpirationTime())) {
            throw new TokenExpiredException("Token expirou!");
        }

        List<String> authorities = claims.getStringListClaim("authorities");

        ApplicationUser applicationUser = ApplicationUser
                .builder()
                .email(email)
                .role(String.join(",", authorities))
                .build();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(applicationUser, null, createAuthorities((authorities)));

        auth.setDetails(signedJWT.serialize());

        return auth;
    }

    private static List<SimpleGrantedAuthority> createAuthorities(List<String> authorities) {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
