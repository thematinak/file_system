package com.example.rest.files.fileRest.controller.entity;

import com.example.rest.files.fileRest.service.entity.FileEntityType;

import java.sql.Timestamp;

public class FilePropsRes {

    private String path;
    private String name;
    private Integer size;
    private Timestamp lastModified;
    private Timestamp created;
    private FileEntityType entityType;

    public FilePropsRes() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Timestamp getLastModified() {
        return lastModified;
    }

    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public FileEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(FileEntityType entityType) {
        this.entityType = entityType;
    }
}
