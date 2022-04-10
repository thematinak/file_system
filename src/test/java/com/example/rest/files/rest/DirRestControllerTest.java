package com.example.rest.files.rest;

import com.example.rest.files.fileRest.controller.DirRestController;
import com.example.rest.files.fileRest.controller.entity.FilePropsRes;
import com.example.rest.files.fileRest.controller.entity.PathReq;
import com.example.rest.files.fileRest.service.ApplicationProperties;
import com.example.rest.files.fileRest.service.FileSystemImpl;
import com.example.rest.files.fileRest.service.MapSupportImpl;
import com.example.rest.files.fileRest.service.entity.FileEntityType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DirRestController.class)
@Import({FileSystemImpl.class, MapSupportImpl.class})
public class DirRestControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    DirRestController controller;

    @MockBean
    ApplicationProperties applicationProperties;


    private static final String root = "./test/dir_rest_controller_test/";


    @BeforeAll
    static void setup() throws IOException {

        Files.createDirectories(Paths.get(root));
        Files.createDirectories(Paths.get(root + "a"));
        Files.createDirectory(Paths.get(root + "a/dir"));
        Files.writeString(Paths.get(root + "a/a.txt"), "aaa");
        Files.writeString(Paths.get(root + "a/b.txt"), "a");
        Files.writeString(Paths.get(root + "a/c.txt"), "aa");
    }

    @BeforeEach
    void prepareMock() {
        Mockito.when(applicationProperties.getRootFilePath()).thenReturn(Paths.get(root));
    }

    @AfterAll
    static void after() throws IOException {
        Path p = Paths.get(root);

        deleteDirectory(p.toFile());
    }

    private static void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }


    @Test
    void getDirectory1() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/v1/dir")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"/a/\"}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void getDirectory2() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/v1/dir")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"/../../a/\"}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDirectory3() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/v1/dir")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"/b/\"}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDirectory4() throws Exception {

        PathReq req = new PathReq();
        req.setPath("/a/");
        List<FilePropsRes> res = controller.getDirectory(req);
        Assert.isTrue(res.size() == 4, "Wrong size");
        Assert.isTrue(FileEntityType.DIR.equals(res.get(0).getEntityType()) && "dir".equals(res.get(0).getName()), "Wrong size");
        Assert.isTrue(FileEntityType.FILE.equals(res.get(1).getEntityType()) && "b.txt".equals(res.get(1).getName()), "Did not sorted");
        Assert.isTrue(FileEntityType.FILE.equals(res.get(2).getEntityType()) && "c.txt".equals(res.get(2).getName()), "Did not sorted");
        Assert.isTrue(FileEntityType.FILE.equals(res.get(3).getEntityType()) && "a.txt".equals(res.get(3).getName()), "Did not sorted");
    }

    @Test
    void createDirectory1() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .put("/v1/dir")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"/createDir/a/b/c\"}")
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated());
        Assert.isTrue(Paths.get(root + "/createDir/a/b/c").toFile().isDirectory(), "Did not created dir");
    }

    @Test
    void createDirectory2() throws Exception {
        Files.createDirectories(Paths.get(root + "/createDir/a2/b2/c2"));
        mvc.perform(MockMvcRequestBuilders
                        .put("/v1/dir")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"/createDir/a2/b2/c2\"}")
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated());
        Assert.isTrue(Paths.get(root + "/createDir/a2/b2/c2").toFile().isDirectory(), "Did not created dir");
    }

    @Test
    void createDirectory3() throws Exception {
        Files.createDirectories(Paths.get(root + "/createDir/a2/b2/c2"));
        mvc.perform(MockMvcRequestBuilders
                        .put("/v1/dir")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"../../../bad/dir\"}")
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }


    @Test
    void deleteDirectory1() throws Exception {
        Files.createDirectories(Paths.get(root + "/createDir/a3/b3/c3"));
        mvc.perform(MockMvcRequestBuilders
                        .delete("/v1/dir")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"/createDir/a3/b3/c3\"}")
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteDirectory2() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .delete("/v1/dir")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"/does/not/exists/\"}")
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteDirectory3() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .delete("/v1/dir")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"/remove/invalid/path/\"}")
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }
}
