package com.okta.developer.theaters.controller;

import com.okta.developer.theaters.model.Location;
import com.okta.developer.theaters.model.Theater;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOpaqueToken;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles({"test"})
public class TheaterControllerTest {

    @Autowired
    private WebTestClient client;

    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:bionic"))
            .withExposedPorts(27017)
            .withEnv("MONGO_INIT_DATABASE", "airbnb");

    @BeforeAll
    public static void setUp() {
        mongoDBContainer.setPortBindings(List.of("27017:27017"));
        mongoDBContainer.start();
    }


    @Test
    public void collectionGet_noAuth_returnsUnauthorized() throws Exception {
        this.client.get().uri("/theater").exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void collectionGet_withValidOpaqueToken_returnsOk() throws Exception {
        this.client.mutateWith(mockOpaqueToken()).get().uri("/theater").exchange().expectStatus().isOk();
    }

    @Test
    public void post_withMissingAuthorities_returnsForbidden() throws Exception {
        Theater theater = new Theater();
        theater.setId("123");
        theater.setLocation(new Location());
        this.client.mutateWith(mockOpaqueToken())
                .post().uri("/theater").body(fromValue(theater))
                .exchange().expectStatus().isForbidden();
    }

    @Test
    public void post_withValidOpaqueToken_returnsCreated() throws Exception {
        Theater theater = new Theater();
        theater.setLocation(new Location());
        this.client.mutateWith(mockOpaqueToken().authorities(new SimpleGrantedAuthority("theater_admin")))
                .post().uri("/theater").body(fromValue(theater))
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.id").isNotEmpty();
    }

    @AfterAll
    public static void tearDown(){
        mongoDBContainer.stop();
    }

}
