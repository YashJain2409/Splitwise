package com.splitwise.service;

import com.splitwise.dao.UserDAO;
import com.splitwise.dto.UpdateUserProfile;
import com.splitwise.dto.UserDTO;
import com.splitwise.exception.ApplicationException;
import com.splitwise.model.User;
import com.splitwise.model.UserAuth;
import com.splitwise.repository.UserAuthRepository;
import com.splitwise.repository.UserRepository;

import java.time.LocalDateTime;

import org.apache.commons.validator.routines.EmailValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserAuthService userAuthService;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


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

        UserDTO userDTO1 = userDAO.saveUser(userDTO);

        User user = modelMapper.map(userDTO1,User.class);
        UserAuth userAuth = new UserAuth();
        userAuth.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userAuth.setUser(user);
        if(userDTO1!=null){
            userAuthService.saveUserAuth(userAuth);
        }

        return userDTO1;
    }
}
