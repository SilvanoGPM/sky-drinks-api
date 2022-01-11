package com.github.skyg0d.skydrinksapi.repository.request;

import com.github.skyg0d.skydrinksapi.domain.ClientRequest;
import com.github.skyg0d.skydrinksapi.domain.ClientRequestAlcoholicDrinkCount;
import com.github.skyg0d.skydrinksapi.domain.ClientRequestDate;
import com.github.skyg0d.skydrinksapi.domain.ClientRequestDrinkCount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ClientRequestRepository extends JpaRepository<ClientRequest, UUID>,
        JpaSpecificationExecutor<ClientRequest> {

    @Query("SELECT DISTINCT new com.github.skyg0d.skydrinksapi.domain.ClientRequestDate(CAST(cr.createdAt AS LocalDate))"
            + " FROM ClientRequest cr"
            + " GROUP BY CAST(cr.createdAt AS LocalDate)"
            + " ORDER BY CAST(cr.createdAt AS LocalDate)"
    )
    List<ClientRequestDate> getAllDatesInRequests();

    @Query("SELECT new com.github.skyg0d.skydrinksapi.domain.ClientRequestDrinkCount(d.uuid, d.name, COUNT(d.name))"
            + " FROM ClientRequest cr"
            + " JOIN cr.drinks d"
            + " JOIN cr.user u"
            + " WHERE u.uuid = ?1"
            + " GROUP BY d.name, d.uuid"
            + " ORDER BY COUNT(d.name) DESC"
    )
    List<ClientRequestDrinkCount> countTotalDrinksInRequest(UUID userUUID, Pageable pageable);

    @Query("SELECT new com.github.skyg0d.skydrinksapi.domain.ClientRequestDrinkCount(d.uuid, d.name, COUNT(d.name))"
            + " FROM ClientRequest cr"
            + " JOIN cr.drinks d"
            + " JOIN cr.user u"
            + " GROUP BY d.name, d.uuid"
            + " ORDER BY COUNT(d.name) DESC"
    )
    List<ClientRequestDrinkCount> countTotalDrinksInRequest(Pageable pageable);

    @Query("SELECT new com.github.skyg0d.skydrinksapi.domain.ClientRequestDrinkCount(d.uuid, d.name, COUNT(d.name))"
            + " FROM ClientRequest cr"
            + " JOIN cr.drinks d"
            + " JOIN cr.user u"
            + " WHERE cr.status = 'CANCELED'"
            + " GROUP BY d.name, d.uuid"
            + " ORDER BY COUNT(d.name) DESC"
    )
    List<ClientRequestDrinkCount> mostCanceledDrinks(Pageable pageable);

    @Query("SELECT new com.github.skyg0d.skydrinksapi.domain.ClientRequestAlcoholicDrinkCount(d.alcoholic, COUNT(d.alcoholic))"
            + " FROM ClientRequest cr"
            + " JOIN cr.drinks d"
            + " JOIN cr.user u"
            + " WHERE u.uuid = ?1"
            + " GROUP BY d.alcoholic"
            + " ORDER BY COUNT(d.alcoholic) DESC"
    )
    List<ClientRequestAlcoholicDrinkCount> countAlcoholicDrinksInRequests(UUID userUUID, Pageable pageable);

}
