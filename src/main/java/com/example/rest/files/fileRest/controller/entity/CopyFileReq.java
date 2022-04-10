package com.example.rest.files.fileRest.controller.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class CopyFileReq {

    @NotBlank(message = "MoveFrom is mandatory")
    @Pattern(regexp = "/([^.\\\\/]+/)*([^\\\\/]+)?", message = "Invalid path")
    private String moveFrom;
    @NotBlank(message = "MoveTo is mandatory")
    @Pattern(regexp = "/([^.\\\\/]+/)*([^\\\\/]+)?", message = "Invalid path")
    private String moveTo;

    public CopyFileReq() {
    }

    public String getMoveFrom() {
        return moveFrom;
    }

    public void setMoveFrom(String moveFrom) {
        this.moveFrom = moveFrom;
    }

    public String getMoveTo() {
        return moveTo;
    }

    public void setMoveTo(String moveTo) {
        this.moveTo = moveTo;
    }
}
