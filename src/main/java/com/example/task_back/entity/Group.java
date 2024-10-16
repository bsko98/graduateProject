package com.example.task_back.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;


@Entity
@ToString(exclude = "userGroups")
@Table(name = "Groups")
@Getter
@Setter
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String groupName;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "group",cascade = CascadeType.REMOVE)
    private Set<UserGroup> userGroups = new HashSet<>();

    public Group() {

    }

    public Group(String groupName) {
        this.groupName = groupName;
    }
}
