package com.example.bachelor.impl;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.stream.Stream;

interface UserRepository extends CrudRepository<UserEntity, String> {
    Optional<UserEntity> findByUsername(String username);

    Stream<UserEntity> findAllBy();
}
