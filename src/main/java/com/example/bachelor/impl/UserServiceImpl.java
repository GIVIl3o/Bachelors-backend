package com.example.bachelor.impl;

import com.example.bachelor.api.NotificationInfo;
import com.example.bachelor.api.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    // TODO
    private final int JWT_EXPIRATION_DAYS = 7;

    // TODO
    private final String JWT_KEY = "secret**";

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final FileService avatarService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not founds"));
    }

    private String persistAndGetSession(String username, String password) {
        if (repository.findByUsername(username).isPresent())
            throw new IllegalArgumentException("username already taken");

        repository.save(new UserEntity(username, encoder.encode(password), Set.of()));
        return generateToken(username);
    }

    @Override
    public String registerWithDefaultAvatar(String username, String password) {
        var jwt = persistAndGetSession(username, password);

        avatarService.copyDefaultAvatar(username);

        return jwt;
    }

    @Override
    public String register(String username, String password, InputStream content) {
        var jwt = persistAndGetSession(username, password);

        avatarService.doUpload(username, content);

        return jwt;
    }

    @Override
    public String generateToken(String username) {
        var expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * JWT_EXPIRATION_DAYS);
        return Jwts.builder().setSubject(username).setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, JWT_KEY).compact();
    }

    private Optional<String> parseJWT(String jwt) {
        try {
            var body = Jwts.parser().setSigningKey(JWT_KEY).parseClaimsJws(jwt).getBody();

            return Optional.of(body.getSubject());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserDetails> parseToken(String token) {
        return token == null ? Optional.empty() : Optional.of(token).flatMap(this::parseJWT).map(this::loadUserByUsername);
    }

    @Override
    public Collection<String> getAllUsernames() {
        return repository.findAllBy().stream().map(UserEntity::getUsername).collect(toSet());
    }

    @Override
    public boolean existsByUsernamesAllIn(Collection<String> usernames) {
        return repository.findAllByUsernameIn(usernames).stream().map(UserEntity::getUsername)
                .collect(toSet()).containsAll(usernames);
    }

    @Override
    public void changeAvatar(String username, InputStream avatar) {
        avatarService.doUpload(username, avatar);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        var user = repository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user:" + username + " not found"));
        
        if (encoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(encoder.encode(newPassword));
            repository.save(user);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect password");
        }
    }
}
