package com.example.task_back.controller;

import com.example.task_back.entity.Prayer;
import com.example.task_back.service.PrayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RestController
public class PrayerController {

    @Autowired
    private PrayerServiceImpl prayerService;


    @GetMapping("/getMyPrayer")
    public List<Prayer> getPrayers() {
        List<Prayer> prayers = prayerService.findPrayer();
        System.out.println("showMyPrayer 호출됨");
        for (Prayer prayer : prayers) {
            System.out.println(prayer.toString());
        }
        return prayers;
    }

    @GetMapping("/getPrayerById/{id}")
    public Optional<Prayer> getPrayerById(@PathVariable("id") Long id){
        return prayerService.findPrayerById(id);
    }


    @PostMapping("/saveMyPrayer")
    public Prayer savePrayer(@RequestBody Prayer prayer){
        prayer.setTimeOfPrayer(LocalDateTime.now());
        return prayerService.savePrayer(prayer);
    }

    @PutMapping("/updatePrayer/{id}")
    public Prayer updatePrayer(@PathVariable("id") Long id, @RequestBody Prayer requestPrayer){
        System.out.println("수정컨트롤러");
        Prayer prayer = prayerService.findById(id).orElseThrow(() -> new IllegalArgumentException("해당하는 아이디가 없습니다 id : " + id));
        System.out.println("제목: "+requestPrayer.getTitle()+"\n내용: "+requestPrayer.getContent());
        prayer.setTitle(requestPrayer.getTitle());
        prayer.setContent(requestPrayer.getContent());
        return prayerService.updatePrayer(prayer);
    }

}
