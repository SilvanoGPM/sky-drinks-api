package com.github.skyg0d.skydrinksapi.repository.table;

import com.github.skyg0d.skydrinksapi.domain.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface TableRepository extends JpaRepository<Table, UUID>, JpaSpecificationExecutor<Table> {

    Optional<Table> findByNumber(int number);

}
