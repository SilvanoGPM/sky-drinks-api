package com.github.skyg0d.skydrinksapi.service;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.domain.PasswordReset;
import com.github.skyg0d.skydrinksapi.exception.BadRequestException;
import com.github.skyg0d.skydrinksapi.property.PasswordResetProperties;
import com.github.skyg0d.skydrinksapi.repository.password.PasswordResetRepository;
import com.github.skyg0d.skydrinksapi.repository.user.ApplicationUserRepository;
import com.github.skyg0d.skydrinksapi.requests.NewPasswordPostRequestBody;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.utility.RandomString;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class PasswordResetService {

    private final ApplicationUserService applicationUserService;
    private final ApplicationUserRepository applicationUserRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final PasswordResetProperties passwordResetProperties;
    private final JavaMailSender mailSender;

    public void create(String userEmail) {
        ApplicationUser userFound = applicationUserService.findByEmail(userEmail);

        log.info("Criando novo código de confirmação para o usuário com o email \"{}\"", userEmail);

        String token = RandomString.make(passwordResetProperties.getTokenLength());

        PasswordReset passwordReset = PasswordReset
                .builder()
                .expireDate(LocalDateTime.now().plusMinutes(passwordResetProperties.getExpireMinutes()))
                .token(token)
                .user(userFound)
                .build();

        sendTokenToUser(userEmail, token, userFound);

        passwordResetRepository.save(passwordReset);
    }

    public PasswordReset tokenIsValid(String token, String userEmail) {
        ApplicationUser user = applicationUserService.findByEmail(userEmail);
        List<PasswordReset> tokens = passwordResetRepository.findByUser(user);

        log.info("Pesquisando código \"{}\", para o usuário com email \"{}\"", token, userEmail);

        Optional<PasswordReset> optionalPasswordReset = tokens
                .stream()
                .filter((passwordReset) -> passwordReset.getToken().equals(token) && !passwordReset.isResetFinished())
                .findFirst();

        if (optionalPasswordReset.isEmpty()) {
            throw new BadRequestException("Código de confirmação inválido!");
        }

        log.info("Código encontrado para o usuário com o email \"{}\"", userEmail);

        PasswordReset tokenFound = optionalPasswordReset.get();

        log.info("Verificando se o token \"{}\" já expirou", tokenFound.getToken());

        boolean tokenExpired = tokenFound.getExpireDate().isBefore(LocalDateTime.now());

        if (tokenExpired) {
            throw new BadRequestException("Código de confirmação para restaurar senha expirou!");
        }

        log.info("Código de confirmação está correto");

        return tokenFound;
    }

    public void resetPassword(NewPasswordPostRequestBody newPasswordPostRequestBody) {
        PasswordReset tokenFound = tokenIsValid(newPasswordPostRequestBody.getToken(), newPasswordPostRequestBody.getEmail());
        tokenFound.setResetFinished(true);

        log.info("Atualizando a senha do usuário com o email: \"{}\"", newPasswordPostRequestBody.getEmail());

        ApplicationUser user = applicationUserService.findByEmail(newPasswordPostRequestBody.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(newPasswordPostRequestBody.getPassword()));

        applicationUserRepository.save(user);
        passwordResetRepository.save(tokenFound);
    }

    @SneakyThrows
    private void sendTokenToUser(String email, String token, ApplicationUser user) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("oficialskydrinks@gmail.com", "SkyDrinks Suporte");
        helper.setTo(email);

        String content = String.format("<h2>Olá, <b><i>%s</i></b></h2>", user.getName())
                + "<p>Você solicitou a recuperação de sua senha.</p>"
                + String.format("<p>Insira esse código para confirmar: <b>%s</b></p>", token)
                + "<p>Ignore caso você lembre sua senha, ou não solicitou isso.</p>";

        helper.setSubject("Código para resetar sua senha");

        helper.setText(content, true);

        log.info("Envinado confirmação para o email \"{}\"", email);

        mailSender.send(message);
    }

}
