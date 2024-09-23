package com.example.task_back.controller;

import com.example.task_back.dto.PrayerDto;
import com.example.task_back.entity.Prayer;
import com.example.task_back.service.PrayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RestController
public class PrayerController {

    @Autowired
    private PrayerServiceImpl prayerService;


    @GetMapping("/getMyPrayer")
    public ResponseEntity<List<PrayerDto>> getPrayers() {
        return ResponseEntity.ok(prayerService.findPrayer());
    }

    @GetMapping("/getPrayerById/{id}")
    public ResponseEntity<PrayerDto> getPrayerById(@PathVariable("id") Long id){
        Optional<PrayerDto> prayerDto = prayerService.findPrayerById(id);
        return prayerDto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/saveMyPrayer")
    public ResponseEntity<PrayerDto> savePrayer(@RequestBody PrayerDto prayerDto){
        System.out.println(prayerDto.toString());
        PrayerDto createdPrayer = prayerService.savePrayer(prayerDto);
        return ResponseEntity.ok(createdPrayer);
    }

    @PutMapping("/updatePrayer/{id}")
    public ResponseEntity<PrayerDto> updatePrayer(@PathVariable("id") Long id, @RequestBody PrayerDto prayerDto){
        Optional<PrayerDto> updatedPrayer = prayerService.updatePrayer(id, prayerDto);
        return updatedPrayer.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deletePrayer/{id}")
    public ResponseEntity<Void> deletePrayerById(@PathVariable("id") Long id){
        System.out.println("delete 시작: "+ id);
        prayerService.deletePrayerById(id);
        return ResponseEntity.noContent().build();
    }

}
