package com.splitwise.dto;

import lombok.Data;

@Data
public class UpdateUserProfile {
    private String email;
    private String pic;
    private String name;
    private String currentPassword;
    private String newPassword;
}
