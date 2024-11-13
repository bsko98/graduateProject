package com.example.task_back.service;

import com.example.task_back.dto.CommentDto;
import com.example.task_back.entity.Comment;
import com.example.task_back.entity.Prayer;
import com.example.task_back.repository.CommentRepository;
import com.example.task_back.repository.PrayerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PrayerRepository prayerRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, PrayerRepository prayerRepository) {
        this.commentRepository = commentRepository;
        this.prayerRepository = prayerRepository;
    }

    public List<CommentDto> getComments(Long id) {
        return commentRepository.findAllByPrayerIdOrderByIdDesc(id).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CommentDto saveComment(Long id, CommentDto commentDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Comment newComment = new Comment();
        Prayer prayer = prayerRepository.findById(commentDto.getPrayerId()).orElseThrow(() -> new EntityNotFoundException("Prayer not found with id: " + id));
        newComment.setPrayer(prayer);
        newComment.setContent(commentDto.getContent());
        newComment.setUsername(username);
        newComment.setCreatedDate(LocalDateTime.now());
        return convertToDTO(commentRepository.save(newComment));
    }


    private CommentDto convertToDTO(Comment comment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setContent(comment.getContent());
        commentDto.setCreatedDate(comment.getCreatedDate());
        commentDto.setCreatedDateTime(comment.getCreatedDate().format(formatter));
        commentDto.setUsername(comment.getUsername());
        return commentDto;
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
