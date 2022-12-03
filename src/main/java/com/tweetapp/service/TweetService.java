package com.tweetapp.service;

import com.tweetapp.exception.TweetAppException;
import com.tweetapp.model.Tweet;
import com.tweetapp.repository.TweetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TweetService {
    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserService userService;

    public List<Tweet> getAllTweets(){
        return tweetRepository.findAll();
    }

    public Tweet createATweet(Tweet tweet, String userName) throws TweetAppException {
        if(userService.usernameIsEmpty(userName))
            throw new TweetAppException("Invalid username");
        tweet.setUsername(userName);
        tweet.setDate(new Date());
        return tweetRepository.saveAndFlush(tweet);
    }

    public List<Tweet> getAllTweetsForAUser(String username) throws TweetAppException {
        if(userService.usernameIsEmpty(username)) {
        	throw new TweetAppException("Invalid username");
        }
        return tweetRepository.findByUsername(username);
    }

    public boolean tweetIsEmpty(Long tweetId){
        return tweetRepository.findById(tweetId).isEmpty();
    }

    public Tweet updateATweet(String username, Long tweetId, Tweet tweet) throws TweetAppException {
        if(tweetIsEmpty(tweetId)) {
        	throw new TweetAppException("Tweet Not present");
        }
        if(userService.usernameIsEmpty(username)) {
        	throw new TweetAppException("Invalid Username");
        }
        Tweet tweetInDb  = tweetRepository.findById(tweetId).get();
        tweetInDb.setTweet(tweet.getTweet());
        return tweetRepository.saveAndFlush(tweetInDb);
    }

    public void deleteTweet(String username, Long tweetId) throws TweetAppException {
        if(tweetIsEmpty(tweetId))
            throw new TweetAppException("Tweet Not present!");
        if(userService.usernameIsEmpty(username))
            throw new TweetAppException("Invalid Username");
        tweetRepository.deleteById(tweetId);
    }

    public Tweet getTweetById(Long tweetId) throws TweetAppException {
        if(tweetIsEmpty(tweetId))
            throw new TweetAppException("Invalid TweetId");
        return tweetRepository.findById(tweetId).get();
    }
}
