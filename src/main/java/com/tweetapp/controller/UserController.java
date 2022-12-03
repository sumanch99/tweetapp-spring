package com.tweetapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.exception.TweetAppException;
import com.tweetapp.model.User;
import com.tweetapp.model.utilityModel.ApiResponse;
import com.tweetapp.model.utilityModel.ChangePassword;
import com.tweetapp.model.utilityModel.LoginModel;
import com.tweetapp.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/v1.0/tweets")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody User users) {
    	log.info("Entered registerUser");
    	try {
    		User createdUser = userService.createUser(users);
    		log.info("User created successfully");
    		return ResponseEntity.ok(ApiResponse.builder()
                    .status(201)
                    .message(createdUser.getFirstName()+" registered successfully")
                    .data(createdUser)
            .build()); 
    	}catch(TweetAppException ex) {
    		ex.printStackTrace();
    		log.warn(ex.getMessage());
    		return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .status(400)
                    .message(ex.getMessage())
                    .data(users)
            .build()); 
    	}catch(Exception e) {
    		e.printStackTrace();
    		log.error(e.getMessage());
    		return ResponseEntity.internalServerError().body(ApiResponse.builder()
                    .status(500)
                    .message("An Unknown error ocurred! Please contact helpdesk of tweet app.")
                    .data(users)
            .build()); 
    	}
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(@RequestBody LoginModel users) {
    	log.info("Entered loginUser");
    	try {
    		LoginModel user = userService.login(users);
    		log.info("Token generated:"+user.getJwt());
    		if(user.getJwt()!=null) {
    			log.info("User Logged in successfully");
    			return ResponseEntity.ok(ApiResponse.builder()
                        .status(200)
                        .message("Login successful")
                        .data(user)
                .build());
    		}
    		log.info("User Login unsuccessful");
    		return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .status(403).message("Login unsuccessful")
            .build());    
    	}catch(TweetAppException ex) {
    		ex.printStackTrace();
    		log.warn(ex.getMessage());
    		return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .status(403)
                    .message(ex.getMessage())
                    .data(users)
            .build()); 
    	}catch(Exception e) {
    		e.printStackTrace();
    		log.error(e.getMessage());
    		return ResponseEntity.internalServerError().body(ApiResponse.builder()
                    .status(500)
                    .message("An Unknown error ocurred! Please contact helpdesk of tweet app.")
                    .data(users)
            .build()); 
    	}
        
    }

    @PostMapping("/{username}/forgot")
    public ResponseEntity<ApiResponse> changePassword(@PathVariable String username, @RequestBody ChangePassword cp) {
    	log.info("Entered changePassword");
    	try {
    		User user = userService.updatePassword(cp,username);
    		log.info("Password reset successful");
            return ResponseEntity.ok().body(ApiResponse.builder()
                    .status(200).message("Password reset successful").data(user)
                    .build());
    	}catch(TweetAppException ex) {
    		ex.printStackTrace();
    		log.warn(ex.getMessage());
    		return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .status(403)
                    .message(ex.getMessage())
                    .data(cp)
            .build()); 
    	}catch(Exception e) {
    		e.printStackTrace();
    		log.error(e.getMessage());
    		return ResponseEntity.internalServerError().body(ApiResponse.builder()
                    .status(500)
                    .message("An Unknown error ocurred! Please contact helpdesk of tweet app.")
                    .data(cp)
            .build()); 
    	}
        
    }

    @GetMapping("/user/search/{username}")
    public ResponseEntity<ApiResponse> searchByUsername(@PathVariable String username) {
        try {
        	List<User> usersList = userService.getUserByRegex(username);
        	if(usersList.isEmpty()) {
        		return ResponseEntity.ok().body(ApiResponse.builder()
                        .status(200).message("No User Found").data(usersList)
                        .build());
        	}
            return ResponseEntity.ok().body(ApiResponse.builder()
                    .status(200).message("Users Found").data(usersList)
                    .build());
        }catch(Exception e) {
    		e.printStackTrace();
    		log.error(e.getMessage());
    		return ResponseEntity.internalServerError().body(ApiResponse.builder()
                    .status(500)
                    .message("An Unknown error ocurred! Please contact helpdesk of tweet app.")
                    .data(null)
            .build()); 
    	}
    	
    }

    @GetMapping("/users/all")
    public ResponseEntity<ApiResponse> getAllUsers(){
    	try {
    		List<User> users = userService.getAllUsers();
    		if(!users.isEmpty())
    			return ResponseEntity.ok(ApiResponse.builder()
                            .status(200).message("Users Found").data(users)
                    .build());
    		log.info("Tweets not fund");
    		return ResponseEntity.ok().body(ApiResponse.builder()
                    .status(200).message("No Users Found").data(users)
            .build());    
    	}catch(Exception e) {
    		e.printStackTrace();
    		log.error(e.getMessage());
    		return ResponseEntity.internalServerError().body(ApiResponse.builder()
                    .status(500)
                    .message("An Unknown error ocurred! Please contact helpdesk of tweet app.")
                    .data(null)
            .build()); 
    	}
    }
}
