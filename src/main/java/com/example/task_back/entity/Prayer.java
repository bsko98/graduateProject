package com.example.task_back.entity;


import com.example.task_back.converter.StringListConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
public class Prayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    @Lob
    private String content;

    //@ManyToOne
    //@JoinColumn(name = "user_id") // 외래 키 컬럼 이름
    @Column
    private LocalDateTime timeOfPrayer; // 기도를 한 시각

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;


    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_username", referencedColumnName = "username")
    private User user;

    @Column
    private String category;

    @Column
    @Convert(converter = StringListConverter.class)
    private List<String> keywords;



}

