package com.example.bachelor.impl;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.stream.Stream;

interface ProjectUserRepository extends CrudRepository<ProjectUserEntity, Long> {

    Stream<ProjectUserEntity> findAllByUsername(String username);

    Optional<ProjectUserEntity> findByProjectIdAndUsername(int projectId, String username);

    void deleteByProjectId(int projectId);

    void deleteByProjectIdAndUsername(int projectId, String username);
}
