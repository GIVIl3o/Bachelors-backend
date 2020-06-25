package com.example.bachelor.impl;

import org.springframework.data.repository.Repository;

interface EpicRepository extends Repository<EpicEntity, Integer> {

    EpicEntity save(EpicEntity entity);

    void deleteByProjectId(int projectId);

    void deleteById(int id);
}
