package com.example.rest.files.fileRest.controller.entity;

import javax.validation.constraints.NotNull;

public class CreateFileReq extends PathReq {

    @NotNull(message = "Content is mandatory")
    private String content;

    public CreateFileReq() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
