package com.example.task_back.controller;

import com.example.task_back.dto.GroupDto;
import com.example.task_back.dto.PrayerDto;
import com.example.task_back.entity.User;
import com.example.task_back.repository.GroupRepository;
import com.example.task_back.repository.UserGroupRepository;
import com.example.task_back.service.PrayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


@RestController
public class PrayerController {

    private final PrayerService prayerService;


    @Autowired
    public PrayerController(PrayerService prayerService){
        this.prayerService = prayerService;
    }

    @GetMapping("/getMyPrayer")
    public ResponseEntity<List<PrayerDto>> getPrayers() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        /*Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        System.out.println("username: "+id+" role: "+role);*/


        return ResponseEntity.ok(prayerService.findPrayer(id));
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

    @GetMapping("/getGroupPrayer")
    public ResponseEntity<List<PrayerDto>> getGroupPrayers(@RequestParam("groupName") String groupName) {
        System.out.println("그룹명: "+groupName);
        return ResponseEntity.ok(prayerService.getGroupPrayers(groupName));
    }

}
