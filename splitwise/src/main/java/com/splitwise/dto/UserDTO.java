package com.splitwise.dto;

import lombok.Data;

@Data
public class UserDTO {

    private int userId;
    private String email;
    private String password;
    private String name;
    private String newPassword;
    private String profilePic;

}
