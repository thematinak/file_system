package com.example.rest.files.fileRest.controller.entity;

public class FileSearchResultRes {

    private String name;
    private String path;
    int lineNumber;
    int columNumber;

    public FileSearchResultRes() {
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

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getColumNumber() {
        return columNumber;
    }

    public void setColumNumber(int columNumber) {
        this.columNumber = columNumber;
    }
}
