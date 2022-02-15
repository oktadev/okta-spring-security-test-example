package com.okta.developer.gateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOidcLogin;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class UserDataControllerTest {

    @Autowired
    private WebTestClient client;

    @Test
    public void get_noAuth_returnsRedirectLogin() {
        this.client.get().uri("/userdata")
            .exchange()
            .expectStatus().is3xxRedirection();
    }

    @Test
    public void get_withOidcLogin_returnsOk() {
        this.client.mutateWith(mockOidcLogin().idToken(token -> token.claim("name", "Mock User")))
            .get().uri("/userdata")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.userName").isNotEmpty()
            .jsonPath("$.idToken").isNotEmpty()
            .jsonPath("$.accessToken").isNotEmpty();
    }
}
