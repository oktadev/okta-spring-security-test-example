package com.okta.developer.gateway.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserDataController {

    @RequestMapping("/userdata")
    @ResponseBody
    public UserData greeting(@AuthenticationPrincipal OidcUser oidcUser,
                             @RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient client) {

        UserData userData = new UserData();
        userData.setUserName(oidcUser.getFullName());
        userData.setIdToken(oidcUser.getIdToken().getTokenValue());
        userData.setAccessToken(client.getAccessToken().getTokenValue());
        return userData;
    }
}