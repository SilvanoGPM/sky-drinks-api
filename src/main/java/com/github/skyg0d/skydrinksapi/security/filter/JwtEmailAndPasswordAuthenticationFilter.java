package com.github.skyg0d.skydrinksapi.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.property.JwtConfigurationProperties;
import com.github.skyg0d.skydrinksapi.security.token.TokenCreator;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
@Log4j2
public class JwtEmailAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfigurationProperties jwtConfigurationProperties;
    private final TokenCreator tokenCreator;

    @Override
    @SneakyThrows
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        log.info("Tentativa de autenticação. . .");

        ApplicationUser applicationUser = new ObjectMapper().readValue(request.getInputStream(), ApplicationUser.class);

        if (applicationUser == null) {
            throw new UsernameNotFoundException("Não foi possível obter o email ou senha");
        }

        log.info("Criando objeto de autenticação para usuário '{}' e chamando ApplicationUserDetailsService findByUsername", applicationUser.getEmail());

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(applicationUser.getEmail(), applicationUser.getPassword(), Collections.emptyList());

        usernamePasswordAuthenticationToken.setDetails(applicationUser);

        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @Override
    @SneakyThrows
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) {
        log.info("Autenticação ocorreu com sucesso para o usuário '{}' gerando o token JWE", auth.getName());

        SignedJWT signedJWT = tokenCreator.createSignedJWT(auth);

        String encryptedToken = tokenCreator.encryptToken(signedJWT);

        log.info("Token gerado com sucesso, adicionando ele ao cabeçalho de resposta");

        String jwtHeaderName = jwtConfigurationProperties.getHeader().getName();
        String jwtHeaderPrefix = jwtConfigurationProperties.getHeader().getPrefix();

        response.addHeader("Access-Control-Expose-Headers", "XSRF-TOKEN, " + jwtHeaderName);
        response.addHeader(jwtHeaderName, jwtHeaderPrefix + encryptedToken);

    }

}
