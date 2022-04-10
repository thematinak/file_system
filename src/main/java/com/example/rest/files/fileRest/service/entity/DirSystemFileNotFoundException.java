package com.example.rest.files.fileRest.service.entity;

import java.io.FileNotFoundException;

public class DirSystemFileNotFoundException extends FileNotFoundException {
    private String path;

    public DirSystemFileNotFoundException(String s, String path) {
        super(s);
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
