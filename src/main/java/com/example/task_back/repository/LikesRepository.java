package com.example.task_back.repository;

import com.example.task_back.entity.Likes;
import com.example.task_back.entity.Prayer;
import com.example.task_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Integer countByPrayer(Prayer prayer);

    /*boolean existsByUserAndPrayer(User user, Prayer prayer);*/
}
