package com.example.bachelor.rest;

import com.example.bachelor.api.NotificationInfo;
import com.example.bachelor.api.ProjectService;
import com.example.bachelor.api.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Log4j2
@RestController
@AllArgsConstructor
public class UserController {

    private final AuthenticationManager manager;
    private final UserService service;
    private final ProjectService projectService;

    @PostMapping("authentication")
    public String authentication(@RequestParam String username, @RequestParam String password) {
        log.info("auth received: {} {}", username, password);

        manager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        log.info("auth success");
        return service.generateToken(username);
    }

    @PostMapping("/user/avatar")
    public void changeAvatar(Authentication authentication, MultipartFile avatar) throws IOException {
        service.changeAvatar(authentication.getName(), avatar.getInputStream());
    }

    @PostMapping("/user/password")
    public void changePassword(Authentication authentication, @RequestParam String oldPassword, @RequestParam String newPassword) {
        service.changePassword(authentication.getName(), oldPassword, newPassword);
    }

    @PostMapping("registration")
    public String registration(@RequestParam String username, @RequestParam String password,
                               @RequestParam(required = false) MultipartFile avatar) {
        try {
            log.info("registration received: {} {} {}", username, password, avatar == null);

            var token = Optional.ofNullable(avatar).isPresent() ?
                    service.register(username, password, avatar.getInputStream()) :
                    service.registerWithDefaultAvatar(username, password);

            log.info("registration success");
            return token;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username duplication:" + e.getMessage());
        }
    }

    @GetMapping("test")
    public String asd() {
        return "yay, authenticated";
    }

    @GetMapping("testEndpoint")
    public String test() {
        return "not authenticated endpoint success";
    }

    @GetMapping("/users")
    public Collection<String> getAllUsernames() {
        return service.getAllUsernames();
    }

    @GetMapping("/users/notifications")
    public Collection<NotificationInfo> findNotifications(Authentication authentication) {
        return projectService.getNotifications(authentication.getName());
    }
}
