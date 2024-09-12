package com.example.task_back.repository;
import com.example.task_back.entity.Prayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrayerRepository extends JpaRepository<Prayer, Long> {

    List<Prayer> findByOrderByIdDesc();
}

