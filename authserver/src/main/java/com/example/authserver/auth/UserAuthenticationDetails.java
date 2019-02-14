package com.example.authserver.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * Created by tomo on 2019-02-10.
 */
public class UserAuthenticationDetails implements Serializable {
    private String sessionId;

    private String clientId;

    private String remoteAddress;

    public UserAuthenticationDetails(HttpServletRequest request) {
        this(new WebAuthenticationDetails(request), request.getParameter("client_id"));
    }

    public UserAuthenticationDetails(WebAuthenticationDetails details, String clientId) {
        this.sessionId = details.getSessionId();
        this.remoteAddress = details.getRemoteAddress();
        this.clientId = clientId;
    }

    public UserAuthenticationDetails(
            @JsonProperty("sessionId") String sessionId,
            @JsonProperty("clientId") String clientId,
            @JsonProperty("remoteAddress") String remoteAddress) {
        this.sessionId = sessionId;
        this.remoteAddress = remoteAddress;
        this.clientId = clientId;
    }
}
