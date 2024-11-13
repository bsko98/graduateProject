package com.example.task_back.controller;

import com.example.task_back.service.LikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LikesController {

    private final LikesService likesService;

    @Autowired
    public LikesController(LikesService likesService) {
        this.likesService = likesService;
    }

    //좋아요 기능
    @PostMapping("/likePost/{id}")
    public ResponseEntity<String> likePost(@PathVariable("id") Long id){
        likesService.likePost(id);
        return ResponseEntity.ok().body("좋아요 적용");
    }

    //좋아요 갯수
    @GetMapping("/getLikeCount/{id}")
    public ResponseEntity<Integer> getLikeCount(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(likesService.getLikesCount(id));
    }

    /*//좋아요 중복 체크
    @GetMapping("/isLiked/{id}")
    public ResponseEntity<Boolean> isLiked(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(likesService.isLiked(id));
    }*/

}
