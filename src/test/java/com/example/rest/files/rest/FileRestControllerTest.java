package com.example.rest.files.rest;

import com.example.rest.files.fileRest.controller.DirRestController;
import com.example.rest.files.fileRest.controller.FileRestController;
import com.example.rest.files.fileRest.service.ApplicationProperties;
import com.example.rest.files.fileRest.service.FileSystemImpl;
import com.example.rest.files.fileRest.service.MapSupportImpl;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileRestController.class)
@Import({FileSystemImpl.class, MapSupportImpl.class})
public class FileRestControllerTest {


    @Autowired
    MockMvc mvc;

    @Autowired
    FileRestController controller;

    @MockBean
    ApplicationProperties applicationProperties;


    private static final String root = "./test/file_rest_controller_test/";


    @BeforeAll
    static void setup() throws IOException {

        Files.createDirectories(Paths.get(root));
        Files.createDirectories(Paths.get(root + "a"));
        Files.writeString(Paths.get(root + "a/a.txt"), "aaa");
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
    void getFile1() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/v1/file")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"/a/a.txt\"}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"path\":\"\\\\a\\\\a.txt\",\"name\":\"a.txt\",\"size\":3,\"content\":\"aaa\"}", false));
    }

    @Test
    void getFil2() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/v1/file")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"/a/b.txt\"}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFil3() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/v1/file")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"../../file.xml\"}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFile() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .put("/v1/file")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"/a/file.xml\", \"content\": \"string\"}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated());
        Assert.isTrue(Paths.get(root + "/a/file.xml").toFile().isFile(), "Did not create file");
        Assert.isTrue("string".equals(Files.readString(Paths.get(root + "/a/file.xml"))), "File content did not match");
    }

    @Test
    void createFile2() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .put("/v1/file")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"../a/file.xml\", \"content\": \"string\"}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

        Assert.isTrue(!Paths.get(root + "/a/file.xml").toFile().isFile(), "File create");
    }

    @Test
    void deleteFile() throws Exception {
        Files.writeString(Paths.get(root + "/a/del.xml"), "text");
        mvc.perform(MockMvcRequestBuilders
                        .delete("/v1/file")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"/a/del.xml\"}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        Assert.isTrue(!Paths.get(root + "/a/del.xml").toFile().isFile(), "File was not deleted");
    }

    @Test
    void deleteFile2() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .delete("/v1/file")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"/a/nothing.xml\"}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

    }

    @Test
    void deleteFile3() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .delete("/v1/file")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"/../important/file.exe\"}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

    }
}
