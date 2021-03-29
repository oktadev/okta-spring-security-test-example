package com.okta.developer.listings.config;

import com.okta.developer.listings.model.AirbnbListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

import javax.annotation.PostConstruct;

@Configuration
public class RestConfiguration {

    @Autowired
    private RepositoryRestConfiguration repositoryRestConfiguration;

    @PostConstruct
    public void setUp(){
        this.repositoryRestConfiguration.setReturnBodyOnCreate(true);
        this.repositoryRestConfiguration.exposeIdsFor(AirbnbListing.class);
    }
}