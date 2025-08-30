package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.enums.Size;
import com.exe201.group1.psgp_be.enums.Status;
import com.exe201.group1.psgp_be.models.Succulent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SucculentRepo extends JpaRepository<Succulent, Integer> {
    boolean existsBySpeciesNameIgnoreCaseAndSize(String speciesName, Size size);
    boolean existsBySpeciesNameIgnoreCaseAndSizeAndIdNot(String speciesName, Size size, int id);
}
