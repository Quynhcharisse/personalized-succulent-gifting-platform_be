package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.Product;
import com.exe201.group1.psgp_be.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {
    List<Product> findAllBySeller(User seller);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Integer id);
}
