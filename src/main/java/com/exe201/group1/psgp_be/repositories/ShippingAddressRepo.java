package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.ShippingAddress;
import com.exe201.group1.psgp_be.models.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShippingAddressRepo extends JpaRepository<ShippingAddress,Integer> {
    List<ShippingAddress> findByUser(User user, Sort sort);

    Optional<ShippingAddress> findFirstByUserAndIsDefaultTrue(User user);
}
