package com.example.rest.files.fileRest.controller;

import com.example.rest.files.fileRest.controller.entity.FilePropsRes;
import com.example.rest.files.fileRest.controller.entity.PathReq;
import com.example.rest.files.fileRest.service.FileSystem;
import com.example.rest.files.fileRest.service.MapSupport;
import com.example.rest.files.fileRest.service.entity.FileEntity;
import com.example.rest.files.fileRest.service.entity.FileEntityType;
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
public class DirRestController {

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

    @PostMapping("/dir")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get sorted content of directory", description = "Get sorted content of directory. Directories have priority over files. Directories are sorted by name. Files are compared by size")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get list content of Directory ordered by size. List can be empty",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FilePropsRes.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid path to Directory", content = @Content),
    })
    public List<FilePropsRes> getDirectory(@Valid @RequestBody PathReq req) throws IOException {
        return fileSystem
                .getFiles(req.getPath())
                .stream()
                .sorted(DirRestController::fileEntityComparator)
                .map(i -> mapSupport.map(i, FilePropsRes.class))
                .collect(Collectors.toList());
    }

    @PutMapping("/dir")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create directory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Directory was created or already existed", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid directory path", content = @Content),
    })
    public void createDirectory(@Valid @RequestBody PathReq req) throws IOException {
        fileSystem.createDirectory(req.getPath());
    }

    @DeleteMapping("/dir")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete directory, Sub directories and files included")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Directory was successfully deleted or did not exist", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid path to directory", content = @Content),
    })
    public void deleteDirectory(@Valid @RequestBody PathReq req) throws IOException {
        fileSystem.deleteDirectory(req.getPath());
    }


    /**
     * Directories have priority over files.
     * Directories are sorted by name
     * Files are compared by size
     */
    private static int fileEntityComparator(FileEntity f1, FileEntity f2) {
        if (!f1.getEntityType().equals(FileEntityType.DIR) && f2.getEntityType().equals(FileEntityType.DIR)) {
            return 1;
        }
        if (f1.getEntityType().equals(FileEntityType.DIR) && !f2.getEntityType().equals(FileEntityType.DIR)) {
            return -1;
        }
        if (f1.getEntityType().equals(FileEntityType.DIR)) {
            return f1.getName().compareTo(f2.getName());
        } else {
            return f1.getSize().compareTo(f2.getSize());
        }
    }

}
