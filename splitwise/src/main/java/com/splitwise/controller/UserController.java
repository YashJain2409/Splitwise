package com.splitwise.controller;

import com.splitwise.dto.UpdateUserProfile;
import com.splitwise.exception.ApplicationException;
import com.splitwise.model.User;
import com.splitwise.repository.UserRepository;
import com.splitwise.service.UserService;

import java.time.LocalDateTime;
import java.util.Date;

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
    public ResponseEntity<User> registerUser(@RequestBody User userDetails) {
        if(userDetails.getName() == null || userDetails.getEmail() == null || userDetails.getPassword() == null || userDetails.getEmail().isEmpty() || userDetails.getName().isEmpty() || userDetails.getPassword().isEmpty()) {
            throw new ApplicationException("0000","Enter valid user details",HttpStatus.BAD_REQUEST);
        }
        userDetails.setCreatedOn(LocalDateTime.now());
        userDetails.setUpdateOn(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.OK).body(userService.saveUser(userDetails));
    }

    @PostMapping("/updateProfile/{id}")
    public ResponseEntity<Void> updateProfile(@RequestBody UpdateUserProfile userDetails, @PathVariable int id) {
         userService.updateProfile(userDetails,id);
         return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
