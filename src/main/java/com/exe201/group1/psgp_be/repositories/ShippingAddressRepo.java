package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.ShippingAddress;
import com.exe201.group1.psgp_be.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingAddressRepo extends JpaRepository<ShippingAddress,Integer> {
    boolean findByUser(User user);
}
