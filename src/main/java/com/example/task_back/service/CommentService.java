package com.example.task_back.service;

import com.example.task_back.dto.CommentDto;
import com.example.task_back.entity.Comment;
import com.example.task_back.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<CommentDto> getComments(Long id) {
        return commentRepository.findAllByPrayerId(id).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /*public Comment saveComment(Long id, Comment comment) {

        return commentRepository.save(comment);
    }*/


    private CommentDto convertToDTO(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setContent(comment.getContent());
        commentDto.setCreatedDate(comment.getCreatedDate());
        commentDto.setUsername(comment.getUsername());
        return commentDto;
    }
}
