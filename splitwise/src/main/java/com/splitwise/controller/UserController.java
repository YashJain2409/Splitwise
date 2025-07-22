package com.splitwise.controller;

import com.splitwise.dto.UpdateUserProfile;
import com.splitwise.dto.UserDTO;
import com.splitwise.exception.ApplicationException;
import com.splitwise.model.User;
import com.splitwise.repository.UserRepository;
import com.splitwise.service.UserService;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/User")
public class UserController {

    @Autowired
    UserService userService;
    @PostMapping("/signup")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDetails) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.saveUser(userDetails));
    }

    @PostMapping("/updateProfile/{id}")
    public ResponseEntity<UserDTO> updateProfile(@RequestBody UserDTO userDTO, @PathVariable int id) {
         return new ResponseEntity<UserDTO>(userService.updateProfile(userDTO,id),HttpStatus.OK);
    }
}
