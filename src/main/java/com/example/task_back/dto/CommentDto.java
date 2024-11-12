package com.example.task_back.dto;


import com.example.task_back.entity.Prayer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CommentDto {

    private Long id;

    private String username;

    private LocalDateTime createdDate;

    private String content;

}
