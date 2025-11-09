package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepo extends JpaRepository<Wallet, Integer> {
}
