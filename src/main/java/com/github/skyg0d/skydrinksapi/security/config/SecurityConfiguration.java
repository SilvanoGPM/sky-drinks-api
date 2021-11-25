package com.github.skyg0d.skydrinksapi.security.config;

import com.github.skyg0d.skydrinksapi.filter.ExceptionHandlerFilter;
import com.github.skyg0d.skydrinksapi.security.filter.JwtEmailAndPasswordAuthenticationFilter;
import com.github.skyg0d.skydrinksapi.property.JwtConfigurationProperties;
import com.github.skyg0d.skydrinksapi.security.token.TokenCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Log4j2
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final JwtConfigurationProperties jwtConfigurationProperties;
    private final TokenCreator tokenCreator;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .addFilterBefore(exceptionHandlerFilter, LogoutFilter.class)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and().addFilter(new JwtEmailAndPasswordAuthenticationFilter(authenticationManager(), jwtConfigurationProperties, tokenCreator))
                .authorizeRequests()
                .antMatchers(jwtConfigurationProperties.getLoginUrl()).permitAll()
                .antMatchers("/**/admin/**").hasRole("ADMIN");
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
