package com.splitwise.service;

import com.splitwise.dto.UpdateUserProfile;
import com.splitwise.exception.ApplicationException;
import com.splitwise.model.User;
import com.splitwise.repository.UserRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public void updateProfile(UpdateUserProfile userDetails,int userId) {
        User user = null;
        try {
            user = userRepository.findById(userId).orElse(null);
        } catch (Exception e) {
            throw new ApplicationException("0001","error occurred in finding user by id", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(user == null)
            throw new ApplicationException("0001","User not found",HttpStatus.NOT_FOUND);
        if(userDetails.getName() != null && !userDetails.getName().isEmpty()) {
            user.setName(userDetails.getName());
        }
        if(userDetails.getEmail() != null && !userDetails.getEmail().isEmpty() && EmailValidator.getInstance().isValid(userDetails.getEmail())) {
            user.setEmail(userDetails.getEmail());
        }
        if(userDetails.getPic() != null && !userDetails.getPic().isEmpty()) {
            user.setProfilePic(userDetails.getPic());
        }
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new ApplicationException("0001","error updating user details.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public User saveUser(User user) {
        User u = null;
        try {
            u = userRepository.save(user);
        }catch (Exception e) {
            throw new ApplicationException("0001","error saving user details.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return u;
    }
}
