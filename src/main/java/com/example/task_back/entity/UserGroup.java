package com.example.task_back.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "usersGroup")
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    private String role;  // 예: "GROUP_LEADER", "MEMBER"

    public UserGroup() {

    }

    public UserGroup(User user, Group group, String role) {
        this.user = user;
        this.group = group;
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserGroup{" +
                "id=" + id +
                ", user=" + user +
                ", group=" + group +
                ", role='" + role + '\'' +
                '}';
    }
}
