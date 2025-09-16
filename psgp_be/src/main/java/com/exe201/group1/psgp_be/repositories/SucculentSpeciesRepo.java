package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.SucculentSpecies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SucculentSpeciesRepo extends JpaRepository<SucculentSpecies, Long> {
    Optional<SucculentSpecies> findBySpeciesNameIgnoreCase(String speciesName);
}
