package com.example.task_back.repository;

import com.example.task_back.entity.Likes;
import com.example.task_back.entity.Prayer;
import com.example.task_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Integer countByPrayer(Prayer prayer);


    @Query("""
            SELECT l.prayer.id
            FROM Likes l
            WHERE l.createAt BETWEEN :startOfWeek AND :endOfWeek
            GROUP BY l.prayer.id
            HAVING COUNT(l) = (
                    SELECT MAX(COUNT(l2))
                    FROM Likes l2
                    WHERE l2.createAt BETWEEN :startOfWeek AND :endOfWeek
                    GROUP BY l2.prayer.id
                )
        """)
    List<Long> getMostLikedPrayer(@Param("startOfWeek")LocalDateTime startOfWeek, @Param("endOfWeek")LocalDateTime endOfWeek);

    /*boolean existsByUserAndPrayer(User user, Prayer prayer);*/
}
