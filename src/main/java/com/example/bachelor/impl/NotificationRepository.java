package com.example.bachelor.impl;

import org.springframework.data.repository.Repository;

import java.util.List;

interface NotificationRepository extends Repository<NotificationEntity, Integer> {

    void save(NotificationEntity entity);

    void saveAll(Iterable<NotificationEntity> entities);

    void flush();

    void deleteById(int id);

    List<NotificationEntity> findByUsername(String username);
}
