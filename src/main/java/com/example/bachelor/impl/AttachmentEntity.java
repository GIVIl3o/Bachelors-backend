package com.example.bachelor.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Builder
@Entity(name = "attachments")
@NoArgsConstructor
@AllArgsConstructor
class AttachmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NonNull
    private Integer taskId;

    @NonNull
    private String filename;

    @NonNull
    private String contentType;

    @NonNull
    private String url;
}
