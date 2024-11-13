package com.example.task_back.controller;

import com.example.task_back.dto.CommentDto;
import com.example.task_back.entity.Comment;
import com.example.task_back.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    @GetMapping("/getComments/{id}")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable("id") Long id){
        System.out.println("sdfsdf: "+id);
        List<CommentDto> commentList = commentService.getComments(id);
        for(CommentDto c : commentList){
            System.out.println(c.toString());
        }
        return ResponseEntity.ok().body(commentList);
    }

    @PostMapping("/saveComment/{id}")
    public ResponseEntity<CommentDto> saveComment(@PathVariable("id") Long id, @RequestBody CommentDto commentDto){
        System.out.println(commentDto.toString());
        return ResponseEntity.ok().body(commentService.saveComment(id,commentDto));
    }

    @DeleteMapping("/deleteComment/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable("id") Long id){
        commentService.deleteComment(id);
        return ResponseEntity.ok().body("삭제완료");
    }

}
