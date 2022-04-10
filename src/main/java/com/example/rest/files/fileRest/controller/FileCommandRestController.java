package com.example.rest.files.fileRest.controller;

import com.example.rest.files.fileRest.controller.entity.CopyFileReq;
import com.example.rest.files.fileRest.controller.entity.FileSearchResultRes;
import com.example.rest.files.fileRest.controller.entity.SearchFileReq;
import com.example.rest.files.fileRest.service.FileSystem;
import com.example.rest.files.fileRest.service.MapSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1")
public class FileCommandRestController {

    private FileSystem fileSystem;
    private MapSupport mapSupport;

    @Autowired
    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Autowired
    public void setMapSupport(MapSupport mapSupport) {
        this.mapSupport = mapSupport;
    }

    @PostMapping("/movefile")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Move file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "File moved", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid directory path", content = @Content),
    })
    public void moveFile(@Valid @RequestBody CopyFileReq req) throws IOException {
        fileSystem.moveFile(req.getMoveFrom(), req.getMoveTo());
    }

    @PostMapping("/copyfile")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Copy file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "File copied", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid directory path", content = @Content),
    })
    public void copyFile(@Valid @RequestBody CopyFileReq req) throws IOException {
        fileSystem.copyFile(req.getMoveFrom(), req.getMoveTo());
    }

    @PostMapping("/searchfile")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Search in files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful search. Return list of files witch content matched with pattern",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FileSearchResultRes.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid directory path or invalid search pattern", content = @Content),
    })
    public List<FileSearchResultRes> searchFile(@Valid @RequestBody SearchFileReq req) throws IOException {
        return fileSystem
                .searchFiles(req.getPath(), req.getSearchPattern(), req.isRecursive())
                .stream()
                .map(i -> mapSupport.map(i, FileSearchResultRes.class))
                .collect(Collectors.toList());
    }
}
