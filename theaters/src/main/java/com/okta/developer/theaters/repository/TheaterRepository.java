package com.okta.developer.theaters.repository;

import com.okta.developer.theaters.model.Theater;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;


public interface TheaterRepository extends ReactiveMongoRepository<Theater, String> {
}