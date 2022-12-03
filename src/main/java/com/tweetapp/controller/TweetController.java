package com.tweetapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.exception.TweetAppException;
import com.tweetapp.model.Tweet;
import com.tweetapp.model.utilityModel.ApiResponse;
import com.tweetapp.service.TweetService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1.0/tweets")
@Slf4j
public class TweetController {
	@Autowired
	private TweetService tweetService;

	@GetMapping("/all")
	public ResponseEntity<ApiResponse> getAllTweets() {
		List<Tweet> tweetList = tweetService.getAllTweets();
		if (!tweetList.isEmpty())
			return ResponseEntity.ok(
					ApiResponse.builder().status(200).message("Tweets Fetched successfully").data(tweetList).build());
		log.info("Tweets not fund");
		return ResponseEntity.ok()
				.body(ApiResponse.builder().status(200).message("No tweets available").data(tweetList).build());

	}

	@GetMapping("/{username}")
	public ResponseEntity<ApiResponse> getAllTweetsForAUser(@PathVariable String username) throws TweetAppException {
		List<Tweet> tweets = tweetService.getAllTweetsForAUser(username);
		if (tweets.isEmpty()) {
			return ResponseEntity.ok(ApiResponse.builder().status(200).message("No tweets found for user " + username)
					.data(tweets).build());
		}
		return ResponseEntity.ok(ApiResponse.builder().status(200)
				.message("Tweets Fetched successfully for user " + username).data(tweets).build());

	}

	@PostMapping("/{username}/add")
	public ResponseEntity<ApiResponse> postTweetForAUser(@RequestBody Tweet tweet, @PathVariable String username)
			throws TweetAppException {
		Tweet createdTweet = tweetService.createATweet(tweet, username);
		return ResponseEntity.ok(ApiResponse.builder().status(201)
				.message("Tweet created successfully for user " + username).data(createdTweet).build());

	}

	@PutMapping("{username}/update/{tweetId}")
	public ResponseEntity<ApiResponse> updateTweet(@RequestBody Tweet tweet, @PathVariable String username,
			@PathVariable Long tweetId) throws TweetAppException {
		Tweet tweetCreated = tweetService.updateATweet(username, tweetId, tweet);
		return ResponseEntity.ok(ApiResponse.builder().status(201)
				.message("Tweets Updated successfully for user " + username).data(tweetCreated).build());

	}

	@DeleteMapping("{username}/delete/{tweetId}")
	public ResponseEntity<ApiResponse> deleteTweet(@PathVariable String username, @PathVariable Long tweetId) throws TweetAppException {
		tweetService.deleteTweet(username, tweetId);
		return ResponseEntity.ok(ApiResponse.builder().status(200).message("Tweet deleted successfully").build());

	}

}
