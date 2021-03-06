package com.example.rest.files.fileRest.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ApplicationPropertiesImpl implements ApplicationProperties {

    private Path rootFilePath;

    @Override
    public Path getRootFilePath() {
        return rootFilePath;
    }

    @Value("${filesystem.root.path:./file_system/}")
    public void setRootFilePath(String rootFilePath) throws IOException {
        this.rootFilePath = Paths.get(rootFilePath).toAbsolutePath();
        if (!this.rootFilePath.toFile().isDirectory()) {
            Files.createDirectories(this.rootFilePath);
        }
    }
}
