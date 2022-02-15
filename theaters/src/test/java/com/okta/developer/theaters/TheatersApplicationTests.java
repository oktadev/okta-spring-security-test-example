package com.okta.developer.theaters;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class TheatersApplicationTests {

    private static final MongoDBContainer mongoDBContainer =
        new MongoDBContainer(DockerImageName.parse("mongo:bionic"))
            .withExposedPorts(27017)
            .withEnv("MONGO_INIT_DATABASE", "airbnb");

    @BeforeAll
    public static void setUp() {
        mongoDBContainer.setPortBindings(List.of("27017:27017"));
        mongoDBContainer.start();
    }

    @Test
    void contextLoads() {
    }

    @AfterAll
    public static void tearDown() {
        mongoDBContainer.stop();
    }
}
