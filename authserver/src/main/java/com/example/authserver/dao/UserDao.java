package com.example.authserver.dao;

import com.example.authserver.model.User;

/**
 * Created by tomo on 2019-02-10.
 */
public interface UserDao {
    User findById(String id);

    User findByEmail(String email);

    User findByUsername(String username);
}
