package com.example.task_back.controller;

import com.example.task_back.entity.Group;
import com.example.task_back.entity.User;
import com.example.task_back.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService=groupService;
    }

    //내가 속한 그룹 조회
    @GetMapping("/myGroup")
    public ResponseEntity<List<Group>> getMyGroups(){
        List<Group> groups = groupService.getMyGroups();
        return ResponseEntity.ok(groups);
    }

    //그룹 생성 (내가 그룹장)
    @PostMapping("/createGroup")
    public ResponseEntity<String> createNewGroup(@RequestBody String groupName){
        System.out.println(groupName);
        Group newGroup = new Group(groupName);
        return ResponseEntity.ok(groupService.createNewGroup(newGroup));
    }
    
    //선택한 그룹 삭제
    @DeleteMapping("/deleteGroup/{groupId}")
    public ResponseEntity<String> deleteGroup(@PathVariable("groupId") Long groupId){
        String result = groupService.deleteGroup(groupId);
        return ResponseEntity.ok(result);
    }    

    //선택한 그룹에서 나오기
    @DeleteMapping("/leaveGroup/{groupId}")
    public ResponseEntity<String>leaveGroup(@PathVariable("groupId")Long groupId){
        String result = groupService.leaveGroup(7L);
        return ResponseEntity.ok("");
    }

    //선택한 그룹의 맴버들을 반환
    @GetMapping("/getGroupMembers")
    public ResponseEntity<List<User>> getGroupMembers(Long groupId){
        List<User> memberList = groupService.getGroupMembers(groupId);
        return ResponseEntity.ok(memberList);
    }

    //그룹에 가익하는 로직
    @PostMapping("/joinGroup")
    public ResponseEntity<String> joinGroup(Long groupId){
        String result = groupService.joinGroup(groupId);
        return ResponseEntity.ok(result);
    }

}
