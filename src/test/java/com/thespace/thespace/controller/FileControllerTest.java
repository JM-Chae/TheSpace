package com.thespace.thespace.controller;

import static com.thespace.thespace.config.RestDocsConfig.restDocsConfig;
import static com.thespace.thespace.config.RestDocsConfig.write;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.thespace.thespace.config.DataBaseCleaner;
import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.dto.board.BoardFileDTO;
import com.thespace.thespace.dto.board.UploadFilesDTO;
import com.thespace.thespace.repository.CategoryRepository;
import com.thespace.thespace.repository.CommunityRepository;
import com.thespace.thespace.service.BoardFileService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles("test")
class FileControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private DataBaseCleaner dataBaseCleaner;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private BoardFileService boardFileService;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentation) {
        mockMvc = restDocsConfig(context, restDocumentation);
        dataBaseCleaner.clear();
    }

    @AfterEach
    void deleteTemp() throws IOException {
        File path = new File("src/test/resources/static/files");
        FileUtils.deleteDirectory(path);
    }

    @Test
    void fileUpload() throws Exception {
        //given
        MockMultipartFile file1 = new MockMultipartFile(
            "fileList",
            "test.png",
            "image/png",
            "<<test.png>>".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
            "fileList",
            "test.txt",
            "text/txt",
            "<<test.txt>>".getBytes()
        );

        Community community = communityRepository.save(new Community("test", "test"));

        Category category = categoryRepository.save(new Category(
            community.getCommunityName(),
            "test",
            "test",
            community,
            new ArrayList<>()
        ));

        //when
        ResultActions result = mockMvc.perform(multipart("/file").file(file1).file(file2));

        //then
        result.andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$[0].fileName").value(file1.getOriginalFilename()),
                jsonPath("$[0].imageChk").value("true"),
                jsonPath("$[1].fileName").value(file2.getOriginalFilename()),
                jsonPath("$[1].imageChk").value("false")
            );

        //docs
        result.andDo(write().document(
            requestParts(partWithName("fileList").description("File list to upload")),
            responseFields(
                fieldWithPath("[].fileId").description("Random generation identifier"),
                fieldWithPath("[].fileName").description("File name to will upload"),
                fieldWithPath("[].imageChk").description("Whether the file is an image."),
                fieldWithPath("[].ord").description(
                    "The number of files in that post when it is posted to a board.")
            )
        ));
    }

    @Test
    void fileGet() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile(
            "fileList",
            "test.png",
            "image/png",
            "test!".getBytes()
        );

        List<MultipartFile> fileList = new ArrayList<>();
        fileList.add(file);
        UploadFilesDTO dto = new UploadFilesDTO(fileList);

        List<BoardFileDTO> boardFileDTOList = boardFileService.upload(dto);

        //when
        ResultActions result = mockMvc.perform(get(
            "/file/{fileid}/{filename}",
            boardFileDTOList.getFirst().fileId(),
            boardFileDTOList.getFirst().fileName()
        ));

        //then
        result.andExpect(status().isOk()).andExpect(jsonPath("$").value("test!"));

        //docs
        result.andDo(write().document(pathParameters(
            parameterWithName("fileid").description("File ID to get"),
            parameterWithName("filename").description("File name to get")
        )));
    }

    @Test
    void fileDelete() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile(
            "fileList",
            "test.png",
            "image/png",
            "test!".getBytes()
        );

        List<MultipartFile> fileList = new ArrayList<>();
        fileList.add(file);
        UploadFilesDTO dto = new UploadFilesDTO(fileList);

        List<BoardFileDTO> boardFileDTOList = boardFileService.upload(dto);

        //when
        ResultActions result = mockMvc.perform(delete(
            "/file/{fileid}/{filename}",
            boardFileDTOList.getFirst().fileId(),
            boardFileDTOList.getFirst().fileName()
        ));

        //then
        result.andExpect(status().isOk());

        //docs
        result.andDo(write().document(pathParameters(
            parameterWithName("fileid").description("File ID to get"),
            parameterWithName("filename").description("File name to get")
        )));
    }
}