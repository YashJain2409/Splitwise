package com.splitwise.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity(name = "friends")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private int groupId;

    @Column(name = "name")
    private String groupName;

    @ManyToMany
    @JoinTable(name = "user_group_map",joinColumns = @JoinColumn(name = "group_id"),inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users;
}
