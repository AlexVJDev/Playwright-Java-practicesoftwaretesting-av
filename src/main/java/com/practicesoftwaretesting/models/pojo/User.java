package com.practicesoftwaretesting.models.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

    public User withPassword(String password) {
     return new User(
     firstName,
     lastName,
     address,
     phone,
     dob,
     password,
     email);
     }
}
