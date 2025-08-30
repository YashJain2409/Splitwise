package com.splitwise.service;

import com.splitwise.model.UserAuth;
import com.splitwise.repository.UserAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService implements UserDetailsService {

    @Autowired
    UserAuthRepository userAuthRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserAuth userAuth = userAuthRepository.findByUserEmail(username);

        if (userAuth == null)
        {
            throw new UsernameNotFoundException("Username not found");
        }
        return User.withUsername(userAuth.getUser().getEmail()).password(userAuth.getPassword()).build();
    }

    public void saveUserAuth(UserAuth userAuth){

        userAuthRepository.save(userAuth);
    }


}
