package com.tweetapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tweetapp.exception.TweetAppException;
import com.tweetapp.model.Comment;
import com.tweetapp.model.LikeTweet;
import com.tweetapp.model.Tweet;
import com.tweetapp.model.User;
import com.tweetapp.model.utilityModel.TweetWithLikeComment;
import com.tweetapp.repository.LikeRepository;

@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private TweetService tweetService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    public TweetWithLikeComment likeATweet(String username, Long tweetId) throws TweetAppException {
        if(userService.isUsernameValid(username)) {
        	throw new TweetAppException("Invalid Username");
        }
        if(tweetService.isTweetIdValid(tweetId)) {
        	throw new TweetAppException("Tweet not found!");
        }
        likeRepository.save(LikeTweet.builder()
                        .tweetId(tweetId).username(username)
                .build());

        List<LikeTweet> likeList = getLikedTweetByTweetId(tweetId);
        List<User> usersList = userService.getAllUsersInList(likeList.stream().map(LikeTweet::getUsername).collect(Collectors.toList()));

        List<Comment> commentsList = commentService.getCommentsByTweetId(tweetId);

        Tweet tweet = tweetService.getTweetById(tweetId);
        return TweetWithLikeComment.builder()
                .id(tweet.getId())
                .userName(tweet.getUsername())
                .tweets(tweet.getTweet())
                .date(tweet.getDate())
                .likedUsers(usersList)
                .commentsList(commentsList)
                .build();
    }

    public List<LikeTweet> getLikedTweetByTweetId(Long tweetId) {
        return likeRepository.findByTweetId(tweetId);
    }
}
