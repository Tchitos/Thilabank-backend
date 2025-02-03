package com.thilabank.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {

    private String firstName;
    private String lastName;
    @Email
    private String email;
    private String phone;
    private String address;
    private String city;
    private String zipcode;
    private String password;
}
