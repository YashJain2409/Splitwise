package com.splitwise.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "user_auth")
@Data
public class UserAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "username",referencedColumnName = "email",unique = true, nullable = false)
    private User user;

    @Column(nullable = false)
    private String password;

}
