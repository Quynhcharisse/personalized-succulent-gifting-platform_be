package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.DiscountProgram;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountProgramRepo extends JpaRepository<DiscountProgram,Integer> {
    boolean existsByNameIgnoreCase(String name);

}
