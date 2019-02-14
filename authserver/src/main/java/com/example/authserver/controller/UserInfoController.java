package com.example.authserver.controller;

import com.example.authserver.auth.UserAuthentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Created by tomo on 2019-02-10.
 */
@RestController
@RequestMapping("/")
public class UserInfoController {
    @RequestMapping("/userinfo")
    public Object getUserInfo(Principal principal) {
        UserAuthentication auth = (UserAuthentication) principal;
        return auth.getPrincipal();
    }
}
