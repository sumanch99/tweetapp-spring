package com.tweetapp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.tweetapp.exception.TweetAppException;
import com.tweetapp.model.User;
import com.tweetapp.model.utilityModel.ChangePassword;
import com.tweetapp.model.utilityModel.LoginModel;
import com.tweetapp.model.utilityModel.MyUserDetails;
import com.tweetapp.repository.UsersRepository;
import com.tweetapp.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Component("userDetailsImpl")
public class UserService implements UserDetailsService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;


    public User createUser(User users) throws TweetAppException {
    	log.info("Entered createUser");
        if(users == null){
            throw new TweetAppException("User passed is null");
        }
        if(usersRepository.findByEmail(users.getEmail()).isPresent()){
            throw new TweetAppException("Email already exists");
        }
        if(usersRepository.findByUsername(users.getUsername()).isPresent()){
            throw new TweetAppException("Username already exists. Try with a different username!");
        }
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        log.info("User is getting created");
        try {
        	return usersRepository.saveAndFlush(users);
        }catch(DataIntegrityViolationException e) {
        	throw new TweetAppException("Insufficient input details provided");
        }
        
    }

    public Map<String, String> login(LoginModel user) throws TweetAppException {
    	
    	Optional<User> users = usersRepository.findByUsername(user.getUserId());
    	if(users.isEmpty()) {
    		users = usersRepository.findByEmail(user.getUserId());
        }
        if(users.isEmpty()) {
        	throw new TweetAppException("Email address or Username not present");
        }
        log.info("User Found to login");
        User u = users.get();
        log.info(u.toString());
        boolean match = passwordEncoder.matches(user.getPassword(), u.getPassword());
        if(match) {
        	UserDetails userDetails = loadUserByUsername(u.getUsername());
        	String jwt = jwtUtil.generateToken(userDetails);
        	Map<String, String> response = new HashMap<>();
        	response.put("username", userDetails.getUsername());
        	response.put("jwt", jwt);
        	return response;
        }else {
        	throw new TweetAppException("Incorrect Credentials");
        }
        

    }

    public User updatePassword(ChangePassword cp,String username) throws TweetAppException {
    	Optional<User> user = usersRepository.findByUsername(username);
        if(user.isEmpty()) {
        	throw new TweetAppException("Username not found");
        }
        User users = user.get();
        if(!passwordEncoder.matches(cp.getOldPassword(), users.getPassword()))
            throw new TweetAppException("Old password doesnot match");

        users.setPassword(passwordEncoder.encode(cp.getNewPassword()));
        return usersRepository.saveAndFlush(users);
    }

    public boolean usernameIsEmpty(String username){
        return usersRepository.findByUsername(username).isEmpty();
    }

    public List<User> getAllUsers(){
        return usersRepository.findAll();
    }

    public List<User> getUserByRegex(String username) {
        return usersRepository.findByUsernameContains(username);
    }

    public User getUserByEmail(String email) throws TweetAppException {
        if(usernameIsEmpty(email))
            throw new TweetAppException("Username Invalid");
        return usersRepository.findByEmail(email).get();
    }

    public List<User> getAllUsersInList(List<String> usernames){
        return usersRepository.findByUsernameIn(usernames);
    }


    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Optional<User> user = usersRepository.findByEmail(userId);

        if(user.isEmpty()) {
        	user = usersRepository.findByUsername(userId);
        }
        if(user.isEmpty()) {
        	throw new UsernameNotFoundException("Email or Username not present");
        }
        return user.map(MyUserDetails::new).get();
    }
    
}
