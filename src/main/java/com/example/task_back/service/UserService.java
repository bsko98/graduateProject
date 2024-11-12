package com.example.task_back.service;

import com.example.task_back.dto.UserRequestDto;
import com.example.task_back.entity.Group;
import com.example.task_back.entity.User;
import com.example.task_back.entity.UserGroup;
import com.example.task_back.enums.UserRole;
import com.example.task_back.repository.UserGroupRepository;
import com.example.task_back.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserGroupRepository userGroupRepository;


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserGroupRepository userGroupRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userGroupRepository = userGroupRepository;
    }


    //회원가입 로직 (에러를 커스텀 에러로 바꿀수도 있음)
    public void registerProcess(UserRequestDto userRequestDto) throws IllegalArgumentException{
        String username = userRequestDto.getUsername();
        String password = userRequestDto.getPassword();
        String nickname = userRequestDto.getNickname();

        //ID중복 확인
        if(userRepository.existsByUsername(username)){
            System.out.println("이미 존재하는 계정");
            throw new IllegalArgumentException("이미 사용중인 ID입니다.");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setNickname(nickname);
        newUser.setRole(UserRole.ROLE_USER);

        userRepository.save(newUser);
    }




}
