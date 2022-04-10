package com.example.rest.files.rest;

import com.example.rest.files.fileRest.controller.FileCommandRestController;
import com.example.rest.files.fileRest.service.ApplicationProperties;
import com.example.rest.files.fileRest.service.FileSystemImpl;
import com.example.rest.files.fileRest.service.MapSupportImpl;
import org.json.JSONArray;
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

@WebMvcTest(FileCommandRestController.class)
@Import({FileSystemImpl.class, MapSupportImpl.class})
public class FileCommandRestControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ApplicationProperties applicationProperties;


    private static final String root = "./test/File_Command_Rest_Controller_Test/";


    @BeforeAll
    static void setup() throws IOException {

        Files.createDirectories(Paths.get(root));
        Files.createDirectories(Paths.get(root + "a"));
        Files.createDirectory(Paths.get(root + "a/dir"));
        Files.writeString(Paths.get(root + "a/move.txt"), "aaa\nbbb\nccc");
        Files.writeString(Paths.get(root + "a/copyfile.txt"), "copyfile\nbbb\ncopyfile");
        Files.writeString(Paths.get(root + "a/a.txt"), "aaa\nbbb\nccc");
        Files.writeString(Paths.get(root + "a/b.txt"), "a");
        Files.writeString(Paths.get(root + "a/c.txt"), "aa");

        Files.createDirectories(Paths.get(root + "/search/b/"));
        Files.createDirectories(Paths.get(root + "/search/c/"));
        Files.writeString(Paths.get(root + "/search/b/move.txt"), "aaa\nbbpattb\nccc");
        Files.writeString(Paths.get(root + "/search/b/copyfile.txt"), "copyfile\nbbb\ncopyfipattle");
        Files.writeString(Paths.get(root + "/search/b/a.txt"), "aaa\npattern\nccc");
        Files.writeString(Paths.get(root + "/search/c/b.txt"), "apatt");
        Files.writeString(Paths.get(root + "/search/c.txt"), "aabbsafpatt");
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
    public void moveFile1() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/v1/movefile")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"moveFrom\": \"/a/move.txt\", \"moveTo\": \"/move/m.txt\"}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated());

        Assert.isTrue(!Paths.get(root + "/a/move.txt").toFile().isFile(), "Did not remove old file");
        Assert.isTrue(Paths.get(root + "/move/m.txt").toFile().isFile(), "Did not create file");
        Assert.isTrue("aaa\nbbb\nccc".equals(Files.readString(Paths.get(root + "/move/m.txt"))), "Did not move content");
    }

    @Test
    public void moveFile2() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/v1/movefile")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"moveFrom\": \"/a/nofile.txt\", \"moveTo\": \"/move/m.txt\"}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

    }


    @Test
    public void moveFile3() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/v1/movefile")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"moveFrom\": \"/a/nofile.txt\", \"moveTo\": \"../../bad/path.txt\"}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void moveFile4() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/v1/movefile")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"moveFrom\": \"/bad/.///path.txt\", \"moveTo\": \"/bad/path.txt\"}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }


    @Test
    public void copyFile1() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/v1/copyfile")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"moveFrom\": \"/a/copyfile.txt\", \"moveTo\": \"/copy/cp.py\"}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated());

        Assert.isTrue(Paths.get(root + "/a/copyfile.txt").toFile().isFile(), "Remove old file");
        Assert.isTrue(Paths.get(root + "/copy/cp.py").toFile().isFile(), "Did not create file");
        Assert.isTrue("copyfile\nbbb\ncopyfile".equals(Files.readString(Paths.get(root + "/copy/cp.py"))), "Did not move content");
    }


    @Test
    public void copyFile2() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/v1/copyfile")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"moveFrom\": \"/a/copyfile_bad.txt\", \"moveTo\": \"/copy/cp.py\"}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

    }

    @Test
    public void copyFile3() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/v1/copyfile")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"moveFrom\": \"/a/copyfile.txt\", \"moveTo\": \"/../../nieco.class\"}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }


    @Test
    public void searchFile1() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/v1/searchfile")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"/search/\", \"searchPattern\": true, \"pattern\": \"nothing\"}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("[]"));

    }

    @Test
    public void searchFile2() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/v1/searchfile")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"/search/\", \"searchPattern\": \"patt\", \"recursive\": false}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[{\"name\":\"c.txt\",\"path\":\"\\\\search\\\\c.txt\",\"lineNumber\":0,\"columNumber\":7}]"));

    }

    @Test
    public void searchFile3() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/v1/searchfile")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"/search/\", \"searchPattern\": \"patt\", \"recursive\": true}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Assert.isTrue((new JSONArray(result.getResponse().getContentAsString())).length() == 5, "Returned bad number of search results");
                });
    }

    @Test
    public void searchFile4() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/v1/searchfile")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"path\": \"/search/bad/path\", \"searchPattern\": \"patt\", \"recursive\": true}")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }



}
