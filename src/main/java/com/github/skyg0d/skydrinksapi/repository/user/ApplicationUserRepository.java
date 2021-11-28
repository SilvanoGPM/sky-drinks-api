package com.github.skyg0d.skydrinksapi.repository.user;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, UUID>, JpaSpecificationExecutor<ApplicationUser> {

    Optional<ApplicationUser> findByEmail(String email);

    Optional<ApplicationUser> findByCpf(String cpf);

}
