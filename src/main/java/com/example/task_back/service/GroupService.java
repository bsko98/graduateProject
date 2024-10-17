package com.example.task_back.service;

import com.example.task_back.dto.GroupDto;
import com.example.task_back.entity.Group;
import com.example.task_back.entity.User;
import com.example.task_back.entity.UserGroup;
import com.example.task_back.enums.GroupRole;
import com.example.task_back.repository.GroupRepository;
import com.example.task_back.repository.UserGroupRepository;
import com.example.task_back.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository,UserGroupRepository userGroupRepository,UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userGroupRepository = userGroupRepository;
        this.userRepository = userRepository;
    }

    public List<Group> getMyGroups() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User myAccount = userRepository.findByUsername(username);
        return userGroupRepository.findGroupByUserId(myAccount.getId());
    }

    public String createNewGroup(GroupDto myGroup) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        User user = userRepository.findByUsername(username);
        Group newGroup = new Group();
        newGroup.setGroupName(myGroup.getGroupName());
        groupRepository.save(newGroup);
        UserGroup userGroup = new UserGroup(user, newGroup, GroupRole.GROUP_LEADER);
        userGroupRepository.save(userGroup);
        return "생성완료";
    }

    /*public String deleteGroup(Long groupId) {
        userGroupRepository.deleteById(groupId);
        groupRepository.deleteById(groupId);
        return "삭제완료";
    }*/

    //테스트 케이스 1. 그룹장일 때 gId8 mId2, 2. 그룹원일 때 gID 7 mId41
    public String leaveGroup(Long groupId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        Long userId = user.getId();
        String userRole = userGroupRepository.findRoleByUserId(userId);
        System.out.println("findIdByUsername 잘 작동하나? "+userId+" 그리고 권한도: "+userRole);
        if(userRole.equals("GROUP_LEADER")){
            userGroupRepository.deleteById(groupId);
            groupRepository.deleteById(groupId);
        }else{
            userGroupRepository.deleteByUserIdAndGroupId(userId,groupId);
        }

        return "삭제완료";
    }


    public List<User> getGroupMembers(Long groupId) {
        List<User> memeberList = userGroupRepository.findAllByGroupId(groupId);
        for(User u : memeberList){
            System.out.println(u.toString());
        }
        return memeberList;
    }


    public String joinGroup(String groupName,String username) {
        User user = userRepository.findByUsername(username);
        System.out.println("user정보에요: "+user.toString());
        Group group = groupRepository.findByGroupName(groupName);
       System.out.println("group 정보에요: "+group.toString());
        UserGroup userGroup = new UserGroup(user, group,GroupRole.GROUP_MEMBER);
        userGroupRepository.save(userGroup);
        return "신규 가입되었습니다.";
    }
}
