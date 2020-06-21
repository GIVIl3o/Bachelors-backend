package com.example.bachelor.impl;

import org.springframework.data.repository.Repository;

interface TaskRepository extends Repository<TaskEntity, Integer> {
    TaskEntity findById(int taskId);

    void save(TaskEntity entity);

    void deleteById(Integer id);
}
