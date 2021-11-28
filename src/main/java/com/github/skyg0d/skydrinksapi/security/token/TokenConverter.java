package com.github.skyg0d.skydrinksapi.security.token;

import com.github.skyg0d.skydrinksapi.property.JwtConfigurationProperties;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Log4j2
@Service
@RequiredArgsConstructor
public class TokenConverter {

    private final JwtConfigurationProperties jwtConfigurationProperties;

    @SneakyThrows
    public String decryptToken(String encryptedToken) {
        log.info("Desencriptando o token. . .");

        JWEObject jweObject = JWEObject.parse(encryptedToken);

        DirectDecrypter directDecrypter = new DirectDecrypter(jwtConfigurationProperties.getPrivateKey().getBytes());

        jweObject.decrypt(directDecrypter);

        log.info("Token decriptado, retornando token assinado");

        return jweObject.getPayload().toSignedJWT().serialize();
    }

    @SneakyThrows
    public void validateSignatureToken(String signedToken) {
        log.info("Iniciando método de validação de token assinado. . .");

        SignedJWT signedJWT = SignedJWT.parse(signedToken);

        log.info("Token convertido, pegando chave pública do token assinado");

        RSAKey publicKey = RSAKey.parse(signedJWT.getHeader().getJWK().toJSONObject());

        log.info("Chave pública encontrada, validando assinatura. . .");

        if (!signedJWT.verify(new RSASSAVerifier(publicKey))) {
            throw new AccessDeniedException("Assinatura de token inválida!");
        }

        log.info("Token tem uma assinatura valida");
    }

}
