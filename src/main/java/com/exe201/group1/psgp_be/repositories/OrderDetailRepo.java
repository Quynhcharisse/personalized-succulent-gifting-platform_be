package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepo extends JpaRepository<OrderDetail, Integer> {
}
