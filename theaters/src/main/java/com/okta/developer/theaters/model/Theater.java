package com.okta.developer.theaters.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("theaters")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Theater {

    @Id
    private String id;
    private Location location;

}