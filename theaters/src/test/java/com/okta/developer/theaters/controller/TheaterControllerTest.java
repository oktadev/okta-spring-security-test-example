package com.okta.developer.theaters.controller;

import com.okta.developer.theaters.model.Location;
import com.okta.developer.theaters.model.Theater;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOpaqueToken;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles({"test", "seed"})
public class TheaterControllerTest {


    @Autowired
    private WebTestClient client;


    @Test
    public void collectionGet_noAuth_returnsUnauthorized() throws Exception {
        this.client.get().uri("/theater").exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void collectionGet_withValidOpaqueToken_returnsOk() throws Exception {
        this.client.mutateWith(mockOpaqueToken()).get().uri("/theater").exchange().expectStatus().isOk();

    }

    @Test
    public void post_withMissingAuthorities_returnsFodbidden() throws Exception{
        Theater theater = new Theater();
        theater.setId("123");
        theater.setLocation(new Location());
        this.client.mutateWith(mockOpaqueToken())
                .post().uri("/theater").body(fromValue(theater))
                .exchange().expectStatus().isForbidden();
    }

    @Test
    public void post_withValidOpaqueToken_returnsCreated() throws Exception{
        Theater theater = new Theater();
        theater.setLocation(new Location());
        this.client.mutateWith(mockOpaqueToken().authorities(new SimpleGrantedAuthority("theater_admin")))
                .post().uri("/theater").body(fromValue(theater))
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.id").isNotEmpty();
    }

    @Test
    public void collectionGet_withInvalidOpaqueToken_returnsUnauthorized() throws Exception {
        this.client.mutateWith(mockOpaqueToken()
                .attributes(attrs -> attrs.put("aud", "ïnvalid"))
                .attributes(attrs -> attrs.put("iss", "ïnvalid"))
                .attributes(attrs -> attrs.put("ext", Instant.MIN)))
                .get().uri("/theater").exchange().expectStatus().isOk();
    }

}