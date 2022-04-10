package com.example.rest.files.fileRest.service.entity;

import java.sql.Timestamp;

public class FileEntity {

    private String path;
    private String name;
    private Long size;
    private Timestamp lastModified;
    private Timestamp created;
    private FileEntityType entityType;

    public FileEntity() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public FileEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(FileEntityType entityType) {
        this.entityType = entityType;
    }
}
