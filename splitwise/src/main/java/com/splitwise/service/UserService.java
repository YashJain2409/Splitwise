package com.splitwise.service;

import com.splitwise.dao.UserDAO;
import com.splitwise.dto.UpdateUserProfile;
import com.splitwise.dto.UserDTO;
import com.splitwise.exception.ApplicationException;
import com.splitwise.model.User;
import com.splitwise.repository.UserRepository;

import java.time.LocalDateTime;

import org.apache.commons.validator.routines.EmailValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ModelMapper modelMapper;


    public UserDTO updateProfile(UserDTO userDTO,int userId) {

        UserDTO user = userDAO.findById(userId);

        if(!user.getPassword().equals(userDTO.getPassword())){
            throw new ApplicationException("Current Password is not correct",HttpStatus.BAD_REQUEST);
        }

        userDTO.setPassword(user.getNewPassword());

        return userDAO.saveUser(userDTO);

    }

    @Transactional
    public UserDTO saveUser(UserDTO userDTO) {

        return userDAO.saveUser(userDTO);
    }
}
