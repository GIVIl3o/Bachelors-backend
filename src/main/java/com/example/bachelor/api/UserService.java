package com.example.bachelor.api;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    String generateToken(String username);

    String registerWithDefaultAvatar(String username, String password);

    String register(String username, String password, InputStream content) throws IOException;

    Collection<String> getAllUsernames();

    Optional<UserDetails> parseToken(String jwt);

    boolean existsByUsernamesAllIn(Collection<String> usernames);
}
