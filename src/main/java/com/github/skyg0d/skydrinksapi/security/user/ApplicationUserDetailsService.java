package com.github.skyg0d.skydrinksapi.security.user;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.repository.user.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ApplicationUserDetailsService implements UserDetailsService {

    private final ApplicationUserRepository applicationUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        log.info("Procurando no BD pelo email: '{}'", email);

        Optional<ApplicationUser> userOptional = applicationUserRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException(String.format("Usuário '%s' não foi encontrado", email));
        }

        ApplicationUser applicationUser = userOptional.get();

        log.info("ApplicationUser encontrado: '{}'", applicationUser);

        return new CustomUserDetails(applicationUser);
    }

    private static final class CustomUserDetails extends ApplicationUser implements UserDetails {
        CustomUserDetails(ApplicationUser applicationUser) {
            super(applicationUser);
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Arrays.stream(getRole().split(","))
                    .map((role) -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        }

        @Override
        public String getUsername() {
            return getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

    }

}
