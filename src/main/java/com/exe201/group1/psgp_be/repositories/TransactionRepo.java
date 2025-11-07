package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepo extends JpaRepository<Transaction, Integer> {
}
