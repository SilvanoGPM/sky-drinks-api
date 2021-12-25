package com.github.skyg0d.skydrinksapi.socket.config;

import com.github.skyg0d.skydrinksapi.property.CorsProperties;
import com.github.skyg0d.skydrinksapi.property.JwtConfigurationProperties;
import com.github.skyg0d.skydrinksapi.util.PrincipalCreatorUtil;
import com.github.skyg0d.skydrinksapi.util.TokenConverterUtil;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.text.ParseException;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Log4j2
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final CorsProperties corsProperties;
    private final JwtConfigurationProperties jwtConfigurationProperties;
    private final TokenConverterUtil tokenConverterUtil;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/sky-drinks")
                .setAllowedOrigins(corsProperties.getOrigins().toArray(new String[0]))
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                String authHeader = jwtConfigurationProperties.getHeader().getName();

                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand()) || StompCommand.CONNECT.equals(accessor.getCommand())) {
                    List<String> tokenList = accessor.getNativeHeader(authHeader);
                    accessor.removeNativeHeader(authHeader);

                    boolean tokenListIsEmpty = tokenList == null || tokenList.isEmpty();
                    String token = tokenListIsEmpty ? null : tokenList.get(0);

                    try {
                        UsernamePasswordAuthenticationToken auth = decryptToken(token);

                        accessor.setUser(auth);

                        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
                    } catch (Exception exception) {
                        throw new MessagingException(exception.getMessage());
                    }
                }

                return message;
            }
        });
    }

    private UsernamePasswordAuthenticationToken decryptToken(String encryptedToken) throws JOSEException, ParseException {
        String prefix = jwtConfigurationProperties.getHeader().getPrefix();

        if (encryptedToken == null || !encryptedToken.startsWith(prefix)) {
            throw new JOSEException("Está faltando o token!");
        }

        String token = encryptedToken.replace(prefix, "");
        SignedJWT signedJWT = tokenConverterUtil.decryptedValidating(token);

        return PrincipalCreatorUtil.createPrincipal(signedJWT);
    }

}
