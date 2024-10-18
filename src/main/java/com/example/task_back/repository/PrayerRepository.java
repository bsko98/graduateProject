package com.example.task_back.repository;
import com.example.task_back.entity.Prayer;
import com.example.task_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;


public interface PrayerRepository extends JpaRepository<Prayer, Long> {

    List<Prayer> findByUserUsernameOrderByIdDesc(String username);
    List<Prayer> findByUserUsernameAndTimeOfPrayerBetween(String username, LocalDateTime startDate, LocalDateTime endDate);
    Integer countByUserUsernameAndTimeOfPrayerBetween(String username,LocalDateTime startDate, LocalDateTime endDate);

    @Query("""
            SELECT p from Prayer p
            WHERE p.id IN(
            SELECT MAX(p2.id)
            FROM Prayer p2
            WHERE p2.user.id IN :userIdList
            GROUP BY p2.user.id
            )
            """)
    List<Prayer> findLatestPrayerForEachUser(@Param("userIdList") List<Long> userIdList);
}

