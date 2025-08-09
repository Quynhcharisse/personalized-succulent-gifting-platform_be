package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepo extends JpaRepository<Account, Integer> {
    Optional<Account> findByEmail(String email);

    Optional<Account> findByEmailAndActive(String email, boolean active);
}
