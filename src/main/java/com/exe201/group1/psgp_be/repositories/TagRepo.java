package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepo extends JpaRepository<Tag, Integer> {
    boolean existsByName(String name);

    Tag findByName(String name);
}
