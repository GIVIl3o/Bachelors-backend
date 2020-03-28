package com.example.bachelor.controllers;

import com.example.bachelor.impl.TestEntity;
import com.example.bachelor.impl.TestRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@AllArgsConstructor
public class test {

    private final TestRepository repository;

    @GetMapping("retrieve")
    public TestEntity retrieve(@RequestParam("id") Integer id) {
        log.info("hey new retrieve request, so wow");

        return repository.findById(id).get();
    }

    @GetMapping("insert")
    public Integer insert(@RequestParam("name") String name) {
        var entity = new TestEntity();
        entity.setTestField(name);
        return repository.save(entity).getId();
    }
}
