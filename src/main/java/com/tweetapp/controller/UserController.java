package com.tweetapp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.exception.TweetAppException;
import com.tweetapp.handler.KafkaProducer;
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

	@Autowired
	KafkaProducer kafkaProducer;

	@PostMapping("/register")
	public ResponseEntity<ApiResponse> registerUser(@RequestBody User users) throws TweetAppException {
		log.info("Entered registerUser");
		User createdUser = userService.createUser(users);
		log.info("User created successfully");
		return ResponseEntity.ok(ApiResponse.builder().status(201)
				.message(createdUser.getFirstName() + " registered successfully").data(createdUser).build());
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse> loginUser(@RequestBody LoginModel users) throws TweetAppException {
		log.info("Entered loginUser");
		Map<String, String> user = userService.login(users);
		log.info("Token generated:" + user.get("jwt"));
		if (user.get("jwt") != null) {
			log.info("User Logged in successfully");
			return ResponseEntity.ok(ApiResponse.builder().status(200).message("Login successful").data(user).build());
		}
		log.info("User Login unsuccessful");
		return ResponseEntity.badRequest()
				.body(ApiResponse.builder().status(403).message("Login unsuccessful").build());

	}

	@PostMapping("/{username}/forgot")
	public ResponseEntity<ApiResponse> changePassword(@PathVariable String username, @RequestBody ChangePassword cp)
			throws TweetAppException {
		log.info("Entered changePassword");
		kafkaProducer.sendMessageToTopic(username); // start kafka
		User user = userService.updatePassword(cp, username);
		log.info("Password reset successful");
		return ResponseEntity.ok()
				.body(ApiResponse.builder().status(200).message("Password reset successful").data(user).build());

	}

	@GetMapping("/user/search/{username}")
	public ResponseEntity<ApiResponse> searchByUsername(@PathVariable String username) {
		List<User> usersList = userService.getUserByRegex(username);
		if (usersList.isEmpty()) {
			return ResponseEntity.ok()
					.body(ApiResponse.builder().status(200).message("No User Found").data(usersList).build());
		}
		return ResponseEntity.ok()
				.body(ApiResponse.builder().status(200).message("Users Found").data(usersList).build());

	}

	@GetMapping("/users/all")
	public ResponseEntity<ApiResponse> getAllUsers() {
		log.info("Entered getAllUsers");
		List<User> users = userService.getAllUsers();
		if (!users.isEmpty()) {
			log.info("Users Found");
			return ResponseEntity.ok(ApiResponse.builder().status(200).message("Users Found").data(users).build());
		}
		log.info("No Users Found");
		return ResponseEntity.ok()
				.body(ApiResponse.builder().status(200).message("No Users Found").data(users).build());
	}
}
