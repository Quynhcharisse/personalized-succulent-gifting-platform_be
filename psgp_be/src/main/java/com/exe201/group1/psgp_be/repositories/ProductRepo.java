package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, Integer> {
}
