package com.example.rest.files.fileRest.controller;

import com.example.rest.files.fileRest.controller.entity.CreateFileReq;
import com.example.rest.files.fileRest.controller.entity.FileEntityRes;
import com.example.rest.files.fileRest.controller.entity.PathReq;
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

@RestController
@RequestMapping("v1")
public class FileRestController {

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

    @PostMapping("/file")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get content of file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get content of file",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FileEntityRes.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid file path", content = @Content),
    })
    public FileEntityRes getFile(@Valid @RequestBody PathReq req) throws IOException {
        FileEntityRes res = mapSupport.map(fileSystem.getFileProperties(req.getPath()), FileEntityRes.class);
        res.setContent(fileSystem.readFile(req.getPath()));
        return res;
    }

    @PutMapping("/file")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create file or update existing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "File created or updated", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid file path", content = @Content),
    })
    public void createFile(@Valid @RequestBody CreateFileReq req) throws IOException {
        fileSystem.writeFile(req.getPath(), req.getContent());
    }

    @DeleteMapping("/file")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File deleted", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid file path", content = @Content),
    })
    public void deleteFile(@Valid @RequestBody PathReq req) throws IOException {
        fileSystem.deleteFile(req.getPath());
    }

}
