package com.example.task_back.repository;
import com.example.task_back.entity.Prayer;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDateTime;
import java.util.List;


public interface PrayerRepository extends JpaRepository<Prayer, Long> {

    List<Prayer> findByOrderByIdDesc();
    List<Prayer> findByTimeOfPrayerBetween(LocalDateTime startDate, LocalDateTime endDate);
    Integer countByTimeOfPrayerBetween(LocalDateTime startDate, LocalDateTime endDate);
}

