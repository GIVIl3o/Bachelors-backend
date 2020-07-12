package com.example.bachelor.impl;

import org.springframework.data.repository.Repository;

import java.util.stream.Stream;

interface CommentRepository extends Repository<CommentEntity, Integer> {

    void save(CommentEntity entity);

    Stream<CommentEntity> findByTaskId(int taskId);
}
