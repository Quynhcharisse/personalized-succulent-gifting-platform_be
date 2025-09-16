package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.ProductAccessory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAccessoryRepo extends JpaRepository<ProductAccessory, Integer> {
    List<ProductAccessory> findByProductId(Integer productId);
}
