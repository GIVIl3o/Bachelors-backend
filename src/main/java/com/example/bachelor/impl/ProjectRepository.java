package com.example.bachelor.impl;

import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

interface ProjectRepository extends Repository<ProjectEntity, Integer> {

    Optional<ProjectEntity> findById(int projectId);

    ProjectEntity save(ProjectEntity project);

    Stream<ProjectEntity> findAllByIdIn(Collection<Integer> projectIds);
}
