package com.example.task_back.service;

import com.example.task_back.entity.Prayer;
import com.example.task_back.repository.PrayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PrayerServiceImpl implements PrayerService {

    @Autowired
    private final PrayerRepository prayerRepository;

    public List<Prayer> getPrayers() {
        return prayerRepository.findAll();
    }


    public Prayer savePrayer(Prayer prayer){
        Prayer newPrayer = new Prayer();
        newPrayer.setId(prayer.getId());
        newPrayer.setTitle(prayer.getTitle());
        newPrayer.setContent(prayer.getContent());
        newPrayer.setTimeOfPrayer(prayer.getTimeOfPrayer());
        return prayerRepository.save(newPrayer);

    }


}
