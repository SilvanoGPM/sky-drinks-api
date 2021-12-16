package com.github.skyg0d.skydrinksapi.security.config;

import com.github.skyg0d.skydrinksapi.enums.Roles;
import com.github.skyg0d.skydrinksapi.exception.details.ExceptionDetails;
import com.github.skyg0d.skydrinksapi.filter.ExceptionHandlerFilter;
import com.github.skyg0d.skydrinksapi.property.JwtConfigurationProperties;
import com.github.skyg0d.skydrinksapi.security.filter.JwtEmailAndPasswordAuthenticationFilter;
import com.github.skyg0d.skydrinksapi.security.filter.JwtTokenAuthorizationFilter;
import com.github.skyg0d.skydrinksapi.security.token.TokenConverter;
import com.github.skyg0d.skydrinksapi.security.token.TokenCreator;
import com.github.skyg0d.skydrinksapi.util.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Log4j2
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final JwtConfigurationProperties jwtConfigurationProperties;
    private final TokenCreator tokenCreator;
    private final TokenConverter tokenConverter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                .and()
                .addFilterBefore(exceptionHandlerFilter, LogoutFilter.class)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((req, res, ex) -> {
                    HttpStatus status = HttpStatus.UNAUTHORIZED;

                    res.setStatus(status.value());
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    res.getWriter().write(ExceptionUtils.convertObjectToJson(ExceptionDetails.createExceptionDetails(ex, status, "Não autorizado")));
                })
                .and()
                .addFilter(new JwtEmailAndPasswordAuthenticationFilter(authenticationManager(), jwtConfigurationProperties, tokenCreator))
                .addFilterAfter(new JwtTokenAuthorizationFilter(jwtConfigurationProperties, tokenConverter), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(jwtConfigurationProperties.getLoginUrl()).permitAll()
                .antMatchers("/**/admin/**").hasRole(Roles.ADMIN.getName())
                .antMatchers("/**/user/**").hasAnyRole(Roles.USER.getName(), Roles.ADMIN.getName())
                .antMatchers("/**/waiter/**").hasAnyRole(Roles.WAITER.getName(), Roles.ADMIN.getName())
                .antMatchers("/**/barmen/**").hasAnyRole(Roles.BARMEN.getName(), Roles.ADMIN.getName())
                .antMatchers("/**/waiter-or-barmen/**").hasAnyRole(Roles.WAITER.getName(), Roles.BARMEN.getName(), Roles.ADMIN.getName())
                .antMatchers("/**/all/**").hasAnyRole(Roles.ADMIN.getName(), Roles.USER.getName(), Roles.WAITER.getName(), Roles.BARMEN.getName())
                .antMatchers("/**").permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
