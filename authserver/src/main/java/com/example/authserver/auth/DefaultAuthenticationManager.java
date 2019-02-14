package com.example.authserver.auth;

import com.example.authserver.model.User;
import com.example.authserver.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * Created by tomo on 2019-02-10.
 */
public class DefaultAuthenticationManager implements AuthenticationManager {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.info("start to authenticate");
        String principal = (String)authentication.getPrincipal();
        logger.info("principal " + principal);
        User user = userService.retrieveUserByEmail(principal);
        if (user == null) {
            user = userService.retrieveUserByUsername(principal);
        }
        if (user == null) {
            throw new UsernameNotFoundException("no user was found with username/email: " + principal);
        } else {
            CharSequence credentials = (CharSequence) authentication.getCredentials();
            if (!passwordEncoder.matches(credentials, user.getPassword())) {
                throw new BadCredentialsException("user credentials not match, username/email: " + principal);
            }
            return new UserAuthentication(
                    new UserPrincipal(user),
                    user.getAuthorities(),
                    new UserAuthenticationDetails((WebAuthenticationDetails) authentication.getDetails(), null));
        }
    }
}
