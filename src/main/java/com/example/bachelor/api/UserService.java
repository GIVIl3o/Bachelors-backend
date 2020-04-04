package com.example.bachelor.api;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {

    String generateToken(String username);

    String register(String username, String password);

    Optional<UserDetails> parseToken(String jwt);

}
