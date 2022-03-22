package com.github.skyg0d.skydrinksapi.repository.user;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.domain.ClientRequestDrinkCount;
import com.github.skyg0d.skydrinksapi.domain.TotalUsers;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, UUID>, JpaSpecificationExecutor<ApplicationUser> {

    Optional<ApplicationUser> findByEmail(String email);

    Optional<ApplicationUser> findByCpf(String cpf);

    @Query("SELECT new com.github.skyg0d.skydrinksapi.domain.TotalUsers("
            + " COUNT(u.name), COUNT(CASE WHEN u.lockRequests = true THEN 1 END),"
            + " COUNT(CASE WHEN u.lockRequests = false THEN 1 END))"
            + " FROM ApplicationUser u"
    )
    TotalUsers countTotalUsers();

}
