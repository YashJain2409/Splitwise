package com.splitwise.dao;

import com.splitwise.dto.UserDTO;
import com.splitwise.exception.ApplicationException;
import com.splitwise.model.User;
import com.splitwise.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserDAO {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    public UserDTO saveUser(UserDTO userDTO) {

        User user = modelMapper.map(userDTO,User.class);

        user.setCreatedOn(LocalDateTime.now());

        try {
            user = userRepository.save(user);
        }catch (Exception e) {

            if(userDTO.getUserId()!=0){
                throw new ApplicationException("error update user details.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            throw new ApplicationException("error saving user details.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return modelMapper.map(user, UserDTO.class);
    }

    public UserDTO findById(int userId){

        User user = userRepository.findById(userId).orElseThrow(()-> new ApplicationException("0001","User details not found for the user id : " + userId,HttpStatus.NOT_FOUND));

        return modelMapper.map(user, UserDTO.class);
    }
}
