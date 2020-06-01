package com.example.bachelor.impl;


import org.springframework.data.repository.Repository;

interface EpicRepository extends Repository<EpicEntity, Integer> {

    EpicEntity save(EpicEntity entity);

    void deleteById(int id);
}
