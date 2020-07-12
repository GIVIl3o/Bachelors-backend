package com.example.bachelor.impl;

import org.springframework.data.repository.Repository;

import java.util.stream.Stream;

interface WatchingRepository extends Repository<WatchingEntity, Integer> {

    void deleteByUsernameAndTaskId(String username, int taskId);

    void save(WatchingEntity entity);

    Stream<WatchingEntity> findByUsername(String username);

    Stream<WatchingEntity> findByTaskId(int taskId);
}
