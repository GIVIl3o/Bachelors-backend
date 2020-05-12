package com.example.bachelor.impl;

import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.stream.Stream;

interface ProjectRepository extends Repository<ProjectEntity, Integer> {

    ProjectEntity save(ProjectEntity project);

    Stream<ProjectEntity> findAllByIdIn(Collection<Integer> projectIds);
}
