package com.example.bachelor.impl;

import com.example.bachelor.api.TaskDetails;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.stream.Stream;

interface TaskRepository extends Repository<TaskEntity, Integer> {
    Optional<TaskEntity> findById(int taskId);

    Optional<TaskEntity> findBySprintIdAndProgressAndLeftIdIsNull(int sprintId, TaskDetails.TaskProgress progress);
    Optional<TaskEntity> findBySprintIdAndProgressAndRightIdIsNull(int sprintId, TaskDetails.TaskProgress progress);

    TaskEntity getById(int taskId);

    @Modifying
    @Query("update tasks task set task.sprintId = null where task.sprintId = :sprintId")
    void unconnectFromSprint(@Param("sprintId") int sprintId);

    Stream<TaskEntity> findByProjectIdAndAssignee(int projectId, String assignee);

    void deleteByProjectId(int projectId);

    void save(TaskEntity entity);

    void deleteById(Integer id);
}
