package com.example.task_back.repository;

import com.example.task_back.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    Group findByGroupName(String groupName);

    @Query("SELECT g.id FROM Group g WHERE g.groupName = :groupName")
    List<Long> findIdByGroupName(@Param("groupName") String groupName);
}
