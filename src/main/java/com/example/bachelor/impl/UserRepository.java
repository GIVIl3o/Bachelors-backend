package com.example.bachelor.impl;

import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

interface UserRepository extends Repository<UserEntity, String> {

    UserEntity save(UserEntity user);

    Optional<UserEntity> findByUsername(String username);

    Set<UserEntity> findAllBy();

    Set<UserEntity> findAllByUsernameIn(Collection<String> usernames);
}
