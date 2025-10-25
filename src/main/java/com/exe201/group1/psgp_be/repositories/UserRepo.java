package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    User findByAccountId(Integer accountId);
}