package com.example.task_back.repository;

import com.example.task_back.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPrayerIdOrderByIdDesc(Long id);
}
