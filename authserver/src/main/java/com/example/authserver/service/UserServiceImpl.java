package com.example.authserver.service;

import com.example.authserver.dao.UserDao;
import com.example.authserver.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by tomo on 2019-02-10.
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public User retrieveUserByEmail(String email) throws UsernameNotFoundException {
        return userDao.findByEmail(email);
    }

    @Override
    public User retrieveUserById(String id) throws UsernameNotFoundException {
        return userDao.findById(id);
    }

    @Override
    public User retrieveUserByUsername(String username) throws UsernameNotFoundException {
        return userDao.findByUsername(username);
    }
}
