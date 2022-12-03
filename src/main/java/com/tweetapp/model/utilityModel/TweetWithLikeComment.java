package com.tweetapp.model.utilityModel;

import com.tweetapp.model.Comment;
import com.tweetapp.model.User;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TweetWithLikeComment {
    private Long id;
    private String userName;
    private String tweets;
    private Date date;
    private List<User> likedUsers;
    private List<Comment> commentsList;
}
