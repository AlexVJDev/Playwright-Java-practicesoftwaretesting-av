package com.practicesoftwaretesting.models.pojo;

import lombok.Data;

@Data
public class Address {
    private final String street = "5th Avenue";
    private final String city = "New York";
    private final String state = "NY";
    private final String country = "Country";
    private final String postal_code = "10022";
}