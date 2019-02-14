package com.example.authserver.service;

import com.example.authserver.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Created by tomo on 2019-02-10.
 */
public interface UserService {
    User retrieveUserByEmail(String email) throws UsernameNotFoundException;

    User retrieveUserById(String id) throws UsernameNotFoundException;

    User retrieveUserByUsername(String username) throws UsernameNotFoundException;
}
