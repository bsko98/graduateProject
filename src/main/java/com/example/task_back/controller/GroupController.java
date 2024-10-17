package com.example.task_back.controller;

import com.example.task_back.dto.GroupDto;
import com.example.task_back.entity.Group;
import com.example.task_back.entity.User;
import com.example.task_back.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<String> createNewGroup(@RequestBody GroupDto myGroup){
        System.out.println(myGroup.toString());
        return ResponseEntity.ok(groupService.createNewGroup(myGroup));
    }
    
    //선택한 그룹 삭제
    /*@DeleteMapping("/deleteGroup/{groupId}")
    public ResponseEntity<String> deleteGroup(@PathVariable("groupId") Long groupId){
        String result = groupService.deleteGroup(groupId);
        return ResponseEntity.ok(result);
    }    */

    //선택한 그룹에서 나오기
    @DeleteMapping("/leaveGroup/{groupId}")
    public ResponseEntity<String>leaveGroup(@PathVariable("groupId")Long groupId){
        String result = groupService.leaveGroup(groupId);
        return ResponseEntity.ok("");
    }

    //선택한 그룹의 맴버들을 반환
    @GetMapping("/getGroupMembers/{groupId}")
    public ResponseEntity<List<User>> getGroupMembers(@PathVariable("groupId")Long groupId){
        System.out.println("그룹 확인: "+groupId);
        List<User> memberList = groupService.getGroupMembers(groupId);
        return ResponseEntity.ok(memberList);
    }

    //그룹에 초대하는 로직
    @PostMapping("/joinGroup")
    public ResponseEntity<String> joinGroup(@RequestBody Map<String, String> usernameAndGroupName){
        String username = usernameAndGroupName.get("username");
        String groupName = usernameAndGroupName.get("groupName");
        System.out.println(username + " : "+ groupName);
        String result = groupService.joinGroup(groupName,username);
        return ResponseEntity.ok(result);
    }

}
