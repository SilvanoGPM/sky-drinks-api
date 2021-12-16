package com.github.skyg0d.skydrinksapi.security.filter;

import com.github.skyg0d.skydrinksapi.exception.details.ExceptionDetails;
import com.github.skyg0d.skydrinksapi.property.JwtConfigurationProperties;
import com.github.skyg0d.skydrinksapi.security.token.TokenConverter;
import com.github.skyg0d.skydrinksapi.util.ExceptionUtils;
import com.github.skyg0d.skydrinksapi.util.SecurityContextUtil;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class JwtTokenAuthorizationFilter extends OncePerRequestFilter {

    private final JwtConfigurationProperties jwtConfigurationProperties;
    private final TokenConverter tokenConverter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(jwtConfigurationProperties.getHeader().getName());

        String prefix = jwtConfigurationProperties.getHeader().getPrefix();

        if (header == null || !header.startsWith(prefix)) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.replace(prefix, "").trim();

        SignedJWT signedJWT = decryptedValidating(token);

        try {
            SecurityContextUtil.setSecurityContext(signedJWT);
            chain.doFilter(request, response);
        } catch (RuntimeException ex) {
            HttpStatus status = HttpStatus.UNAUTHORIZED;

            response.setStatus(status.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(ExceptionUtils.convertObjectToJson(ExceptionDetails.createExceptionDetails(ex, status, "Não autorizado.")));
        }
    }

    @SneakyThrows
    private SignedJWT decryptedValidating(String encryptedToken) {
        String signedToken = tokenConverter.decryptToken(encryptedToken);
        return validate(signedToken);
    }

    @SneakyThrows
    private SignedJWT validate(String signedToken) {
        tokenConverter.validateSignatureToken(signedToken);
        return SignedJWT.parse(signedToken);
    }

}
