package com.github.skyg0d.skydrinksapi.repository.request;

import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ClientRequestRepository extends JpaRepository<ClientRequest, UUID>,
        JpaSpecificationExecutor<ClientRequest> {
}
