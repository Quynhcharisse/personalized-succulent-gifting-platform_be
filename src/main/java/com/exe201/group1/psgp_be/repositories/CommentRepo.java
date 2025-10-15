package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepo extends JpaRepository<Comment, Integer> {
}
