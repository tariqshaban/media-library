package com.multimedia.media_library.service;

import com.multimedia.media_library.model.FileInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileInformationServiceTest {
    FileInfoService fileInfoService;

    @BeforeEach
    void setUp() {
        fileInfoService = new FileInfoService();
    }

    @Test
    void givenFilePath_whenGetFileInfo_thenReturnFileInfo() {
        FileInformation fileInformation = fileInfoService.getFileInformation("src/test/resources/sample-video.mp4");

        assertEquals("sample-video.mp4", fileInformation.getId());
        assertEquals("sample-video", fileInformation.getName());
        assertEquals("mp4", fileInformation.getExtension());
        assertEquals(2397942, fileInformation.getSize());
    }
}
