package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.AccountRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRequestRepo extends JpaRepository<AccountRequest, Integer> {
    Optional<AccountRequest> findByEmail(String email);
}
