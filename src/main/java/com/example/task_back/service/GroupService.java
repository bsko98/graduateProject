package com.example.task_back.service;

import com.example.task_back.entity.Group;
import com.example.task_back.entity.User;
import com.example.task_back.entity.UserGroup;
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

    public String createNewGroup(Group group) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        User user = userRepository.findByUsername(username);
        Group newGroup = groupRepository.save(group);
        UserGroup userGroup = new UserGroup(user, newGroup, "GROUP_READER");
        userGroupRepository.save(userGroup);
        return "생성완료";
    }

    public String deleteGroup(Long groupId) {
        userGroupRepository.deleteById(groupId);
        groupRepository.deleteById(groupId);
        return "삭제완료";
    }

    public String leaveGroup(Long groupId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userRepository.findIdByUsername(username);
        System.out.println("findIdByUsername 잘 작동하나? "+userId);
        userGroupRepository.deleteByUserIdAndGroupId(userId,groupId);
        return"";
    }


    public List<User> getGroupMembers(Long groupId) {
        List<User> memeberList = userGroupRepository.findAllByGroupId(7L);
        for(User u : memeberList){
            System.out.println(u.toString());
        }
        return memeberList;
    }


    public String joinGroup(Long groupId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        System.out.println("user정보에요: "+user.toString());
        Optional<Group> group = groupRepository.findById(groupId);
        UserGroup userGroup = new UserGroup(user, group.orElse(null),"GROUP_MEMBER");
        userGroupRepository.save(userGroup);
        return"굿";
    }
}
