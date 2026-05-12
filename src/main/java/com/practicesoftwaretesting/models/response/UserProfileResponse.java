package com.practicesoftwaretesting.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.practicesoftwaretesting.models.pojo.Address;
import lombok.Data;

@Data
public class UserProfileResponse {

    private String id;
    private String provider;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String phone;
    private String dob;
    private String email;
    @JsonProperty("totp_enabled")
    private boolean totpEnabled;
    @JsonProperty("created_at")
    private String createdAt;
    private Address address;
}
