package com.example.authserver.config;

/**
 * Created by tomo on 2019-02-10.
 */

import com.example.authserver.auth.DefaultAuthenticationManager;
import com.example.authserver.auth.EnhancedUserAuthenticationConverter;
import com.example.authserver.service.DefaultUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.sql.DataSource;

/**
 * The Class OAuth2Config defines the authorization server that would
 * authenticate the user and define the client that seeks authorization on the
 * resource owner's behalf.
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
    @Bean
    AuthenticationManager authenticationManager() {
        return new DefaultAuthenticationManager();
    }

    @Autowired
    private DataSource dataSource;

    @Value("${jwt.token.verification-key}")
    private String jwtTokenVerificationKey;

    @Value("${jwt.token.signing-key}")
    private String jwtTokenSigningKey;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
//    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Bean
    public UserDetailsService userDetailsService() {
        return new DefaultUserDetailsService();
    }

    @Bean
    public JwtTokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        DefaultAccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
        EnhancedUserAuthenticationConverter userAuthenticationConverter = new EnhancedUserAuthenticationConverter();
        tokenConverter.setUserTokenConverter(userAuthenticationConverter);
        converter.setAccessTokenConverter(tokenConverter);
        converter.setSigningKey(jwtTokenSigningKey);
        if (converter.isPublic()) {
            converter.setVerifierKey(jwtTokenVerificationKey);
        }
        return converter;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security)
            throws Exception {
        security
                .passwordEncoder(passwordEncoder())
                .tokenKeyAccess("permitAll()") // default is denyAll()
                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints)
            throws Exception {
        endpoints
                .authenticationManager(authenticationManager())
                .tokenStore(tokenStore()).accessTokenConverter(accessTokenConverter())
                .approvalStoreDisabled();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients)
            throws Exception {
        clients.jdbc(dataSource).passwordEncoder(passwordEncoder());
//            .withClient("test")
//                .authorizedGrantTypes("authorization_code", "client_credentials")
//                .authorities("ROLE_CLIENT")
//                .autoApprove(true)
//                .scopes("read", "write")
//                .secret("secret")
//            .and()
//        .build();
    }
}

