package com.example.task_back.service;

import com.example.task_back.entity.Prayer;
import com.example.task_back.repository.PrayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PrayerServiceImpl implements PrayerService {

    @Autowired
    private final PrayerRepository prayerRepository;

    public List<Prayer> findPrayer() {
        return prayerRepository.findByOrderByIdDesc();
    }

    public Optional<Prayer> findPrayerById(Long id){
        return prayerRepository.findById(id);
    }


    public Prayer savePrayer(Prayer prayer){
        return prayerRepository.save(prayer);
    }

    public Prayer updatePrayer(Prayer prayer){
        return prayerRepository.save(prayer);
    }

    public Optional<Prayer> findById(Long id){
        return prayerRepository.findById(id);
    }

}
