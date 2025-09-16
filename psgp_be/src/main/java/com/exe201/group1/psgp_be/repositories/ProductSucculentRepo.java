package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.ProductSucculent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSucculentRepo extends JpaRepository<ProductSucculent, Integer> {
    List<ProductSucculent> findByProductId(Integer productId);
}
