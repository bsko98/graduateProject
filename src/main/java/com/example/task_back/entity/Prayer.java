package com.example.task_back.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Prayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;
    @Column
    private String content;

    //@ManyToOne
    //@JoinColumn(name = "user_id") // 외래 키 컬럼 이름
    @Column
    private LocalDateTime timeOfPrayer; // 기도를 한 시각

    @Override
    public String toString() {
        return "Prayer{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", timeOfPrayer=" + timeOfPrayer +
                '}';
    }
}

