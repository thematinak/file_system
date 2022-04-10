package com.example.rest.files.fileRest.controller;

import com.example.rest.files.fileRest.controller.entity.ErrorEntity;
import com.example.rest.files.fileRest.service.entity.DirSystemFileNotFoundException;
import com.example.rest.files.fileRest.service.entity.FileSystemFileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class RestExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FileSystemFileNotFoundException.class)
    public ResponseEntity<ErrorEntity> handleFileSystemFileNotFoundException(FileSystemFileNotFoundException e) {

        ErrorEntity entity = new ErrorEntity();
        entity.setMessage("File does not exists '"+e.getPath()+"'");
        entity.setStatus(HttpStatus.BAD_REQUEST.toString());
        return new ResponseEntity<>(entity, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DirSystemFileNotFoundException.class)
    public ResponseEntity<ErrorEntity> handleFileSystemFileNotFoundException(DirSystemFileNotFoundException e) {

        ErrorEntity entity = new ErrorEntity();
        entity.setMessage("File does not exists '"+e.getPath()+"'");
        entity.setStatus(HttpStatus.BAD_REQUEST.toString());
        return new ResponseEntity<>(entity, HttpStatus.BAD_REQUEST);
    }
}
