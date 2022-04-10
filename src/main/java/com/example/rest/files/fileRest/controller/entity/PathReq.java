package com.example.rest.files.fileRest.controller.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class PathReq {

    @NotBlank(message = "Path is mandatory")
    @Pattern(regexp = "/([^.\\\\/]+/)*([^\\\\/]+)?", message = "Invalid path")
    protected String path;

    public PathReq() {
    }

    public PathReq(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
