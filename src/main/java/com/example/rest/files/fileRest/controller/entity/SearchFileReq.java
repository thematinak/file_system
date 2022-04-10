package com.example.rest.files.fileRest.controller.entity;

import javax.validation.constraints.NotBlank;

public class SearchFileReq extends PathReq {

    @NotBlank
    private String searchPattern;
    private boolean recursive;

    public SearchFileReq() {
    }

    public String getSearchPattern() {
        return searchPattern;
    }

    public void setSearchPattern(String searchPattern) {
        this.searchPattern = searchPattern;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }
}
