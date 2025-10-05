package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.Succulent;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SucculentRepo extends JpaRepository<Succulent, Integer> {
}
