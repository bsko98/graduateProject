package com.example.task_back.service;

import com.example.task_back.entity.Likes;
import com.example.task_back.entity.Prayer;
import com.example.task_back.entity.User;
import com.example.task_back.repository.LikesRepository;
import com.example.task_back.repository.PrayerRepository;
import com.example.task_back.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LikesService {

    private final LikesRepository likesRepository;
    private final UserRepository userRepository;
    private final PrayerRepository prayerRepository;

    @Autowired
    public LikesService(LikesRepository likesRepository, UserRepository userRepository, PrayerRepository prayerRepository) {
        this.likesRepository = likesRepository;
        this.userRepository = userRepository;
        this.prayerRepository = prayerRepository;
    }

    public void likePost(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Likes newLikes = new Likes();
        User user = userRepository.findByUsername(username);
        Prayer prayer = prayerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Prayer not found with id: " + id));
        newLikes.setUser(user);
        newLikes.setPrayer(prayer);
        newLikes.setCreateAt(LocalDateTime.now());
        likesRepository.save(newLikes);
    }

    public Integer getLikesCount(Long id) {
        Prayer prayer = prayerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Prayer not found with id: " + id));
        return likesRepository.countByPrayer(prayer);
    }


}
