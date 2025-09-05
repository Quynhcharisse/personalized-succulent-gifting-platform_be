package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepo extends JpaRepository<StockMovement, Long> {
}
