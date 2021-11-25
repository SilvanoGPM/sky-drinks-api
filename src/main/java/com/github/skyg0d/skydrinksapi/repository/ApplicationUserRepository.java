package com.github.skyg0d.skydrinksapi.repository;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, UUID> {

    Optional<ApplicationUser> findByEmail(String email);

}