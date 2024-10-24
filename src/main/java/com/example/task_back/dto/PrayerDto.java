package com.example.task_back.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
public class PrayerDto {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime timeOfPrayer; // 기도를 한 시각
    private String username;
    private String userNickname;
    private Long userId;
    private Boolean isPublic;
    private String category;
    private String keywords;

}
