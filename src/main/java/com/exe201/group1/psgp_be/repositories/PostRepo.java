package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepo extends JpaRepository<Post, Integer> {
}
