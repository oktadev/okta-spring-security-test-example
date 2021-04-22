package com.okta.developer.listings.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "airbnb")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirbnbListing {

    @Id
    private String id;
    private String name;
    private String summary;
    @Field(name = "property_type")
    private String propertyType;
    @Field(name = "room_type")
    private String roomType;
    @Field(name = "bed_type")
    private String bedType;
    @Field(name = "cancellation_policy")
    private String cancellationPolicy;

}
