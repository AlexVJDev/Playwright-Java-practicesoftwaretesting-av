package com.practicesoftwaretesting.models.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class User {
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private Address address;
    private String phone;
    private String dob;
    private String password;
    private String email;
}
