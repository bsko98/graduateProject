package com.example.task_back.controller;

import com.example.task_back.dto.UserRequestDto;
import com.example.task_back.entity.Group;
import com.example.task_back.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String singInProcess(UserRequestDto userRequestDto){
        return"login";
    }

    @PostMapping("/signUp")
    public ResponseEntity<String> registerUser(@RequestBody UserRequestDto userRequestDto){
        try{
            System.out.println(userRequestDto.toString());
            userService.registerProcess(userRequestDto);
            return ResponseEntity.ok("회원가입 성공");
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
