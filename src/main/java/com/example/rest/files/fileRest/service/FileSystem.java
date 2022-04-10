package com.example.rest.files.fileRest.service;

import com.example.rest.files.fileRest.service.entity.FileEntity;
import com.example.rest.files.fileRest.service.entity.FileSearchResult;

import java.io.IOException;
import java.util.List;

public interface FileSystem {

    String readFile(String filePath) throws IOException;

    FileEntity getFileProperties(String filePath) throws IOException;

    void copyFile(String sourceFilePath, String destinationFilePath) throws IOException;

    void moveFile(String sourceFilePath, String destinationFilePath) throws IOException;

    void writeFile(String filePath, String content) throws IOException;

    void deleteFile(String filePath) throws IOException;

    void createDirectory(String dirPath) throws IOException;

    void deleteDirectory(String dirPath) throws IOException;

    List<FileEntity> getFiles(String dirPath) throws IOException;

    List<FileSearchResult> searchFiles(String dirPath, String pattern, boolean recursive) throws IOException;


}
