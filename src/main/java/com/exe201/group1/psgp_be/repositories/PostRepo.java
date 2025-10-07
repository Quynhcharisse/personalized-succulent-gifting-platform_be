package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.enums.Status;
import com.exe201.group1.psgp_be.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepo extends JpaRepository<Post, Integer> {
    List<Post> findAllBySellerId(Integer id);

    List<Post> findAllByStatus(Status status);
}
