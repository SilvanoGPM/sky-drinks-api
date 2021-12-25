package com.github.skyg0d.skydrinksapi.util;

import com.github.skyg0d.skydrinksapi.security.token.TokenConverter;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenConverterUtil {

    private final TokenConverter tokenConverter;

    @SneakyThrows
    public SignedJWT decryptedValidating(String encryptedToken) {
        String signedToken = tokenConverter.decryptToken(encryptedToken);
        return validate(signedToken);
    }

    @SneakyThrows
    public SignedJWT validate(String signedToken) {
        tokenConverter.validateSignatureToken(signedToken);
        return SignedJWT.parse(signedToken);
    }
    
}
