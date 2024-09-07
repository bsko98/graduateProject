package com.example.task_back.controller;

import com.example.task_back.entity.Prayer;
import com.example.task_back.repository.PrayerRepository;
import com.example.task_back.service.PrayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RestController
public class PrayerController {

    @Autowired
    private PrayerRepository prayerRepository;


    @GetMapping("/showMyPrayer")
    public List<Prayer> getPrayers() {
        List<Prayer> prayers = prayerRepository.findAll();
        System.out.println("showMyPrayer 호출됨");
        return prayers;
    }


    @PostMapping("/saveMyPrayer")
    public ResponseEntity<Prayer> savePrayer(@RequestBody Prayer prayer){
        System.out.println(prayer.getTitle()+" "+prayer.getContent()+"\n 어떻게 되는거지?");
        prayer.setTimeOfPrayer(LocalDateTime.now());
        return ResponseEntity.ok(prayerRepository.save(prayer));
    }

}
