package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.SucculentSpecies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SucculentSpeciesRepo extends JpaRepository<SucculentSpecies, Long> {
    boolean existsBySpeciesNameIgnoreCase(String speciesName);
    SucculentSpecies findBySpeciesNameIgnoreCase(String speciesName);
}

