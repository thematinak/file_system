package com.example.rest.files.fileRest.service;

import com.example.rest.files.fileRest.service.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service("FileSystem")
public class FileSystemImpl implements FileSystem {

    private ApplicationProperties applicationProperties;

    @Override
    public String readFile(String filePath) throws IOException {
        return Files.readString(getPath(filePath), StandardCharsets.UTF_8);
    }

    @Override
    public FileEntity getFileProperties(String filePath) throws IOException {
        Path sourcePath = getPath(filePath);
        if (!sourcePath.toFile().isFile()) {
            throw new FileSystemFileNotFoundException("File does not exist " + sourcePath, filePath);
        }
        FileEntity entity = new FileEntity();
        mapFileEntity(sourcePath, entity);
        return entity;
    }

    @Override
    public void copyFile(String sourceFilePath, String destinationFilePath) throws IOException {
        Path sourcePath = getPath(sourceFilePath);
        if (!sourcePath.toFile().isFile()) {
            throw new FileSystemFileNotFoundException("File does not exist " + sourcePath, sourceFilePath);
        }
        Path destinationFile = getPath(destinationFilePath);
        if(!destinationFile.getParent().toFile().isDirectory()) {
            Files.createDirectories(destinationFile.getParent());
        }
        Files.copy(sourcePath, destinationFile, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void moveFile(String sourceFilePath, String destinationFilePath) throws IOException {
        Path sourceFile = getPath(sourceFilePath);
        if (!sourceFile.toFile().isFile()) {
            throw new FileSystemFileNotFoundException("File does not exist " + sourceFile, sourceFilePath);
        }
        Path destinationFile = getPath(destinationFilePath);
        if(!destinationFile.getParent().toFile().isDirectory()) {
            Files.createDirectories(destinationFile.getParent());
        }
        Files.move(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    @Override
    public void writeFile(String filePath, String content) throws IOException {
        Path sourceFile = getPath(filePath);
        Files.createDirectories(sourceFile.getParent());
        Files.writeString(sourceFile, content, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    @Override
    public void deleteFile(String filePath) throws IOException {
        Path sourceFile = getPath(filePath);
        File f = sourceFile.toFile();
        if (f.exists() && f.isFile()) {
            Files.deleteIfExists(sourceFile);
        }
    }

    @Override
    public void createDirectory(String dirPath) throws IOException {
        Path sourceFile = getPath(dirPath);
        Files.createDirectories(sourceFile);
    }

    @Override
    public void deleteDirectory(String dirPath) {
        Path sourceFile = getPath(dirPath);
        File f = sourceFile.toFile();
        if (f.exists() && f.isDirectory()) {
            deleteDirectory(f);
        }
    }

    @Override
    public List<FileEntity> getFiles(String dirPath) throws IOException {
        Path sourcePath = getPath(dirPath);
        if (!sourcePath.toFile().isDirectory()) {
            throw new DirSystemFileNotFoundException("Directory does not exist " + sourcePath, dirPath);
        }
        return Files.walk(sourcePath, 1)
                .filter(p -> !sourcePath.equals(p))
                .map(p -> {
                    FileEntity entity = new FileEntity();
                    try {
                        mapFileEntity(p, entity);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                    return entity;
                }).collect(Collectors.toList());
    }

    @Override
    public List<FileSearchResult> searchFiles(String dirPath, String pattern, boolean recursive) throws IOException {
        final Pattern patt = Pattern.compile(pattern);
        Path sourcePath = getPath(dirPath);
        if (!sourcePath.toFile().isDirectory()) {
            throw new DirSystemFileNotFoundException("Directory does not exist " + sourcePath, dirPath);
        }
        return Files.walk(sourcePath, recursive? Integer.MAX_VALUE : 1)
                .filter(p -> p.toFile().isFile())
                .map(p -> {
                    try {
                        String fileContent = Files.readString(p, StandardCharsets.UTF_8);
                        Matcher matcher = patt.matcher(fileContent);
                        boolean matchFound = matcher.find();
                        if (matchFound) {
                            int start = matcher.start();
                            int line = 0, column = 0;
                            for (int i = 0; i < start; i++) {
                                if (fileContent.charAt(i) == '\n') {
                                    line++;
                                    column = 0;
                                } else {
                                    column++;
                                }
                            }

                            FileSearchResult searchResult = new FileSearchResult();
                            mapFileEntity(p, searchResult);
                            searchResult.setColumNumber(column);
                            searchResult.setLineNumber(line);
                            return searchResult;
                        } else {
                            return null;
                        }
                    } catch (IOException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Path getPath(String path) {
        String tmpPath = path.replace("\\", "/");
        tmpPath = tmpPath.startsWith("/") ? tmpPath : ('/' + tmpPath);
        return Paths.get(applicationProperties.getRootFilePath() + tmpPath);
    }

    private String getStringPath(Path p) {
        return p.toFile().getAbsolutePath().substring(applicationProperties.getRootFilePath().toFile().getAbsolutePath().length());
    }

    private void mapFileEntity(Path p, FileEntity entity) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(p, BasicFileAttributes.class);
        File f = p.toFile();
        entity.setName(f.getName());
        if (f.isFile()) {
            entity.setSize(attr.size());
        } else {
            entity.setSize((long) Objects.requireNonNull(p.toFile().listFiles()).length);
        }
        entity.setPath(getStringPath(p));
        entity.setCreated(new Timestamp(attr.creationTime().toMillis()));
        entity.setLastModified(new Timestamp(attr.lastModifiedTime().toMillis()));
        entity.setEntityType(f.isFile() ? FileEntityType.FILE : FileEntityType.DIR);
    }

    private void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }

    @Autowired
    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }
}
