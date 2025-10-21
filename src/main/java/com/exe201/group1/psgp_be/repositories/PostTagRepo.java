package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostTagRepo extends JpaRepository<PostTag, Integer> {
    List<PostTag> findAllByPostId(Integer postId);
}
