package com.practicesoftwaretesting.models.pojo;

import lombok.Data;

@Data
public class User {
    String firstName = "Jhon";
    String lastName = "Smith";
    Address address = new Address();
    String phone = "501 212-3435";
    String dob = "123";
    String password = "StrongPassword";
    String email = "dood@gmail.com";
}
