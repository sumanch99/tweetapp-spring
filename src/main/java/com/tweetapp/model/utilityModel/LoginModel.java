package com.tweetapp.model.utilityModel;

import lombok.Data;

@Data
public class LoginModel {
    private String userId;
    private String password;
    private String jwt;
}
