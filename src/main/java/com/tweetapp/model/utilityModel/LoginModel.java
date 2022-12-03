package com.tweetapp.model.utilityModel;

import lombok.Data;

@Data
public class LoginModel {
    private String email;
    private String username;
    private String password;
    private String jwt;
}
