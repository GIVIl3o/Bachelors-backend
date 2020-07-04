package com.example.bachelor.impl;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

interface TaskRepository extends Repository<TaskEntity, Integer> {
    TaskEntity findById(int taskId);

    @Modifying
    @Query("update tasks task set task.sprintId = null where task.sprintId = :sprintId")
    void unconnectFromSprint(@Param("sprintId") int sprintId);

    Stream<TaskEntity> findByProjectIdAndAssignee(int projectId, String assignee);

    void deleteByProjectId(int projectId);

    void save(TaskEntity entity);

    void deleteById(Integer id);
}
