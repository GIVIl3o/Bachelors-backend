package com.example.bachelor.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class test {

    @GetMapping("test")
    public String test(){
        log.info("hey new request, so wow");
        return "hello world";
    }
}
