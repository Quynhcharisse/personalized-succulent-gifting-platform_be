package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.enums.Size;
import com.exe201.group1.psgp_be.models.Succulent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SucculentRepo extends JpaRepository<Succulent, Integer> {
    boolean existsBySpecies_SpeciesNameIgnoreCaseAndSize(String speciesName, Size size);

    boolean existsBySpecies_SpeciesNameIgnoreCaseAndSizeAndIdNot(String speciesName, Size size, Integer id);

    List<Succulent> findAllByOrderByIdDesc();
}
