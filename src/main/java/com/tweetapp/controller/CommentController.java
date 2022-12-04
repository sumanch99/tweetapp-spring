package com.tweetapp.controller;

import com.tweetapp.exception.TweetAppException;
import com.tweetapp.model.Comment;
import com.tweetapp.model.utilityModel.ApiResponse;
import com.tweetapp.model.utilityModel.TweetWithLikeComment;
import com.tweetapp.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0/tweets")
public class CommentController {
	@Autowired
	private CommentService commentService;

	@PostMapping(("/{username}/reply/{tweetId}"))
	public ResponseEntity<ApiResponse> commentATweet(@RequestBody Comment comments, @PathVariable String username,
			@PathVariable Long tweetId) throws TweetAppException {
		TweetWithLikeComment tweet = commentService.commentATweet(comments, username, tweetId);
		return ResponseEntity.ok(ApiResponse.builder().status(200).message("Commented on the tweet").data(tweet).build());
	}
}
