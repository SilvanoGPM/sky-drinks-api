package com.github.skyg0d.skydrinksapi.security.token;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.property.JwtConfigurationProperties;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class TokenCreator {

    private final JwtConfigurationProperties jwtConfigurationProperties;

    @SneakyThrows
    public SignedJWT createSignedJWT(Authentication auth) {
        log.info("Inicializando criação do token JWT");

        ApplicationUser applicationUser = (ApplicationUser) auth.getPrincipal();

        JWTClaimsSet jwtClaimsSet = createJWTClaimsSet(auth, applicationUser);

        KeyPair rsaKeys = generateKeyPair();

        log.info("Construindo JWK a partir das chaves RSA");

        JWK jwk = new RSAKey
                .Builder((RSAPublicKey) rsaKeys.getPublic())
                .keyID(UUID.randomUUID().toString())
                .build();

        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .jwk(jwk)
                .type(JOSEObjectType.JWT)
                .build();

        SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet);

        log.info("Assinando o token com a chave RSA privada");

        RSASSASigner signer = new RSASSASigner(rsaKeys.getPrivate());

        signedJWT.sign(signer);

        log.info("Token serializado '{}'", signedJWT.serialize());

        return signedJWT;
    }

    public String encryptToken(SignedJWT signedJWT) throws JOSEException {
        log.info("Iniciando a encriptação do token");

        DirectEncrypter directEncrypter = new DirectEncrypter(jwtConfigurationProperties.getPrivateKey().getBytes());

        JWEHeader jweHeader = new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256)
                .contentType("JWT")
                .build();

        JWEObject jweObject = new JWEObject(jweHeader, new Payload(signedJWT));

        log.info("Encriptando o token com o sistema de chave privada");

        jweObject.encrypt(directEncrypter);

        log.info("Token foi encriptado");

        return jweObject.serialize();
    }

    private JWTClaimsSet createJWTClaimsSet(Authentication auth, ApplicationUser applicationUser) {
        log.info("Criando JWTClaimSet para '{}'", applicationUser);

        Date expirationTime = new Date(System.currentTimeMillis() + (jwtConfigurationProperties.getExpiration() * 1000L));

        List<String> authorities = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JWTClaimsSet.Builder()
                .subject(applicationUser.getEmail())
                .claim("authorities", authorities)
                .issuer("https://github.com/SkyG0D")
                .issueTime(new Date())
                .expirationTime(expirationTime)
                .build();
    }

    @SneakyThrows
    private KeyPair generateKeyPair() {
        log.info("Gerando chaves RSA 2048 bits");

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

        generator.initialize(2048);

        return generator.genKeyPair();
    }

}
