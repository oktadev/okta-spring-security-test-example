package com.okta.developer.listings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.developer.listings.model.AirbnbListing;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "seed"})
public class AirbnbListingMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
}
