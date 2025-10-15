package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Integer> {
}
