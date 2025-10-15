package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepo extends JpaRepository<ProductImage, Integer> {
}
