package com.example.bachelor.impl;

import org.springframework.data.repository.Repository;

import java.util.stream.Stream;

interface AttachmentRepository extends Repository<AttachmentEntity, Integer> {

    void save(AttachmentEntity entity);

    Stream<AttachmentEntity> findByTaskId(int taskId);
}
