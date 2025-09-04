package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.enums.Size;
import com.exe201.group1.psgp_be.models.Succulent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SucculentRepo extends JpaRepository<Succulent, Integer> {
    boolean existsBySpecies_SpeciesNameIgnoreCaseAndSize(String speciesName, Size size);
    boolean existsBySpecies_SpeciesNameIgnoreCaseAndSizeAndIdNot(String speciesName, Size size, int id);
}
