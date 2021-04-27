package com.okta.developer.listings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.developer.listings.model.AirbnbListing;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
public class AirbnbListingMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        this.mockMvc.perform(get("/listing")).andExpect(status().isUnauthorized());
    }

    @Test
    public void collectionGet_withValidJwtToken_returnsOk() throws Exception {
        this.mockMvc.perform(get("/listing").with(jwt())).andExpect(status().isOk());
    }

    @Test
    public void save_withMissingAuthorities_returnsForbidden() throws Exception {
        AirbnbListing listing = new AirbnbListing();
        listing.setName("test");
        String json = objectMapper.writeValueAsString(listing);
        this.mockMvc.perform(post("/listing").content(json).with(jwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    public void save_withValidJwtToken_returnsCreated() throws Exception {
        AirbnbListing listing = new AirbnbListing();
        listing.setName("test");
        String json = objectMapper.writeValueAsString(listing);
        this.mockMvc.perform(post("/listing").content(json).with(jwt().authorities(new SimpleGrantedAuthority("listing_admin"))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @AfterAll
    public static void tearDown(){
        mongoDBContainer.stop();
    }

}
