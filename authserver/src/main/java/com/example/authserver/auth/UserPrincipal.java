package com.example.authserver.auth;

import com.example.authserver.model.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.security.Principal;

/**
 * Created by tomo on 2019-02-10.
 */
public class UserPrincipal implements Principal, Serializable {
    private String id;
    private String name;
    private String email;
    private String companyId;
    private String region;

    public UserPrincipal(User user) {
        this(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCompanyId(),
            user.getRegion());
    }

    @JsonCreator
    public UserPrincipal(
            @JsonProperty("id") String id,
            @JsonProperty("name") String username,
            @JsonProperty("email") String email,
            @JsonProperty("companyId") String companyId,
            @JsonProperty("region") String region) {
        this.id = id;
        this.name = username;
        this.email = email;
        this.companyId = companyId;
        this.region = region;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getRegion() {
        return region;
    }
}
