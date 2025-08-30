package com.splitwise.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

@Data
public class UserDTO {

    private int userId;
    private String email;
    private String password;
    private String name;
    private String newPassword;
    private String profilePic;

}
