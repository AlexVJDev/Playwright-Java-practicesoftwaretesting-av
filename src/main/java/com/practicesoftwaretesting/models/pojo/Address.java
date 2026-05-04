package com.practicesoftwaretesting.models.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Address {
    private String street;
    @JsonProperty("house_number")
    private String houseNumber;
    private String city;
    private String state;
    private String country;
    @JsonProperty("postal_code")
    private String postalCode;
}