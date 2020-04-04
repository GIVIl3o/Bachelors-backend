package com.example.bachelor.rest;

import com.example.bachelor.api.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Log4j2
@RestController
@AllArgsConstructor
public class UserController {

    private final AuthenticationManager manager;
    private final UserService service;

    @PostMapping("authentication")
    public String authentication(@RequestParam String username, @RequestParam String password) {
        log.info("auth received: {} {}", username, password);

        manager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        log.info("auth success");
        return service.generateToken(username);
    }

    @PostMapping("registration")
    public String registration(@RequestParam String username, @RequestParam String password) {
        try {
            log.info("registration received: {} {}", username, password);
            var token = service.register(username, password);
            log.info("registration success");
            return token;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username duplication");
        }
    }

    @GetMapping("test")
    public String asd() {
        return "yay, authenticated";
    }

    @GetMapping("testEndpoint")
    public String test(){
        return "not authenticated endpoint success";
    }

}
