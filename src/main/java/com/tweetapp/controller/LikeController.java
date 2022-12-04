package com.tweetapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.exception.TweetAppException;
import com.tweetapp.model.utilityModel.ApiResponse;
import com.tweetapp.model.utilityModel.TweetWithLikeComment;
import com.tweetapp.service.LikeService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1.0/tweets")
@Slf4j
public class LikeController {
	@Autowired
	private LikeService likeService;

	@PutMapping("/{username}/like/{tweetId}")
	public ResponseEntity<ApiResponse> likeATweet(@PathVariable String username, @PathVariable Long tweetId) throws TweetAppException {
		log.info("Entered likeATweet");
		TweetWithLikeComment tweet = likeService.likeATweet(username, tweetId);
		log.info("Liked the tweet");
		return ResponseEntity.ok(ApiResponse.builder().status(200).message("Liked the tweet")
				.data(tweet).build());

	}
}
