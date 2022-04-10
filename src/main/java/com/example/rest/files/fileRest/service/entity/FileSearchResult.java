package com.example.rest.files.fileRest.service.entity;

public class FileSearchResult extends FileEntity {
    int lineNumber;
    int columNumber;

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
