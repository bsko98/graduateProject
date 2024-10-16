package com.example.task_back.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserRequestDto {
    private String username;

    private String password;

    private String nickname;

    private String role;
}
