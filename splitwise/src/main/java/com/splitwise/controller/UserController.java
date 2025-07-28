package com.splitwise.controller;

import com.splitwise.dto.UserDTO;
import com.splitwise.exception.ApplicationException;
import com.splitwise.service.UserService;
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
        if(userDetails.getName() == null || userDetails.getEmail() == null || userDetails.getPassword() == null || userDetails.getEmail().isEmpty() || userDetails.getName().isEmpty() || userDetails.getPassword().isEmpty()) {
            throw new ApplicationException("0000","Enter valid user details",HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.saveUser(userDetails));
    }

    @PostMapping("/updateProfile/{id}")
    public ResponseEntity<UserDTO> updateProfile(@RequestBody UserDTO userDetails, @PathVariable int id) {

         return new ResponseEntity<UserDTO>(userService.updateProfile(userDetails,id),HttpStatus.OK);
    }
}
