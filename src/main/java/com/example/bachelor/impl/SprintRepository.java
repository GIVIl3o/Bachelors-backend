package com.example.bachelor.impl;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface SprintRepository extends Repository<SprintEntity, String> {

    SprintEntity save(SprintEntity entity);

    Optional<SprintEntity> findById(int sprintId);

    @Modifying
    @Query("update sprints sprint set sprint.epicId = null where sprint.epicId = :epicId")
    void unconnectFromEpic(@Param("epicId") int epicId);

    void deleteByProjectId(int projectId);

    void deleteById(int sprintId);
}
