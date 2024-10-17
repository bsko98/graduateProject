package com.example.task_back.repository;

import com.example.task_back.entity.Group;
import com.example.task_back.entity.User;
import com.example.task_back.entity.UserGroup;
import com.example.task_back.enums.GroupRole;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    @Query("SELECT ug.group FROM UserGroup ug WHERE ug.user.id = :userId")
    List<Group> findGroupByUserId(@Param("userId") Long userId);
    
    void deleteByUserIdAndGroupId(Long userId, Long groupId);

    @Query("SELECT ug.user FROM UserGroup ug WHERE ug.group.id = :groupId")
    List<User> findAllByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT ug.role FROM UserGroup ug WHERE ug.user.id = :userId")
    String findRoleByUserId(@Param("userId")Long userId);
}
