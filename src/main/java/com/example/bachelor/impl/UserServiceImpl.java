package com.example.bachelor.impl;

import com.example.bachelor.api.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

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
    private final AvatarService avatarService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not founds"));
    }

    private String persistAndGetSession(String username, String password) {
        repository.save(new UserEntity(username, encoder.encode(password)));
        return generateToken(username);
    }

    @Override
    public String registerWithDefaultAvatar(String username, String password) {
        var jwt = persistAndGetSession(username, password);

        avatarService.copyDefaultAvatar(username);

        return jwt;
    }

    @Override
    public String register(String username, String password, InputStream content) throws IOException {
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
        return repository.findAllBy().map(UserEntity::getUsername).collect(toSet());
    }
}
