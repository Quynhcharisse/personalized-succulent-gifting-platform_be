package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppConfigRepo extends JpaRepository<AppConfig, Integer> {
    Optional<AppConfig> findByKey(String key);
}
