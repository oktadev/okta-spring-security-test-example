package com.okta.developer.gateway.controller;

import lombok.Data;

@Data
public class UserData {

    private String userName;
    private String idToken;
    private String accessToken;
}
