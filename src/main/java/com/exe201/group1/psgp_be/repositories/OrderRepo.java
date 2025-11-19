package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.Order;
import com.exe201.group1.psgp_be.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Integer> {
    List<Order> findByBuyerOrderByOrderDateDesc(User buyer);
}
