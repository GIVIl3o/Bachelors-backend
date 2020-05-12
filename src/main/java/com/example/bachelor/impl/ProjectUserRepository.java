package com.example.bachelor.impl;

import org.springframework.data.repository.CrudRepository;

import java.util.stream.Stream;

interface ProjectUserRepository extends CrudRepository<ProjectUserEntity, Long> {

    Stream<ProjectUserEntity> findAllByUsername(String username);
}
