package com.multimedia.media_library.service;

import com.multimedia.media_library.dto.FileMetadataDetailedResponse;
import com.multimedia.media_library.dto.FileMetadataResponse;
import com.multimedia.media_library.dto.UploadFileRequest;
import com.multimedia.media_library.dto.VideoServingResponse;
import com.multimedia.media_library.exception.ValidationException;
import com.multimedia.media_library.mapper.FileMetadataDetailedMapperImpl;
import com.multimedia.media_library.mapper.FileMetadataMapperImpl;
import com.multimedia.media_library.model.FileInformation;
import com.multimedia.media_library.model.MediaInformation;
import com.multimedia.media_library.model.Violation;
import com.multimedia.media_library.utils.file_validator.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import static java.util.List.of;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MediaLibraryServiceTest {
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String ROLE = "ROLE";
    private static final String SAMPLE_VIDEO_ID = "sample-video.mp4";
    private static final String SAMPLE_VIDEO_NAME = "sample-video";
    private static final String SAMPLE_VIDEO_EXTENSION = "mp4";
    private static final String SAMPLE_VIDEO_MIME = "video/mp4";
    private static final int SAMPLE_VIDEO_SIZE = 1024;
    private static final String VIOLATION_MESSAGE = "VIOLATION_MESSAGE";

    @TempDir
    private Path tempDir;
    private final GetFileValidator getFileValidator = mock(GetFileValidator.class);
    private final AddFileValidator addFileValidator = mock(AddFileValidator.class);
    private final RenameFileValidator renameFileValidator = mock(RenameFileValidator.class);
    private final DeleteFileValidator deleteFileValidator = mock(DeleteFileValidator.class);

    private MediaLibraryService mediaLibraryService;

    @BeforeEach
    void setUp() {
        FileInfoService fileInfoService = mock(FileInfoService.class);
        MediaInfoService mediaInfoService = mock(MediaInfoService.class);
        FileValidatorRegistry fileValidatorRegistry = new FileValidatorRegistry(getFileValidator, addFileValidator, renameFileValidator, deleteFileValidator);

        mediaLibraryService = new MediaLibraryService(fileInfoService, mediaInfoService, fileValidatorRegistry, new FileMetadataMapperImpl(), new FileMetadataDetailedMapperImpl(), tempDir.normalize().toString());

        Path file = tempDir.resolve(USERNAME).resolve(SAMPLE_VIDEO_ID);
        when(fileInfoService.getFileInformation(file.toFile().getPath())).thenReturn(
                FileInformation.builder()
                        .id(SAMPLE_VIDEO_ID)
                        .name(SAMPLE_VIDEO_NAME)
                        .extension(SAMPLE_VIDEO_EXTENSION)
                        .size(SAMPLE_VIDEO_SIZE)
                        .build()
        );
        when(mediaInfoService.getMediaInfo(file.toFile().getPath())).thenReturn(
                MediaInformation.builder()
                        .generalInfo(getGeneralInfo())
                        .videoInfo(getVideoInfo())
                        .audioInfo(getAudioInfo())
                        .build()
        );
        when(getFileValidator.validate(any(), any())).thenReturn(of());
        when(addFileValidator.validate(any(), any())).thenReturn(of());
        when(renameFileValidator.validate(any(), any())).thenReturn(of());
        when(deleteFileValidator.validate(any(), any())).thenReturn(of());

        setupSecurity();
    }

    @Test
    void givenNoFiles_whenGettingFiles_thenReturnEmptyMetadata() throws IOException {
        Files.createDirectories(tempDir.resolve(USERNAME));

        List<FileMetadataResponse> fileMetadataResponse = mediaLibraryService.getFiles();

        assertThat(fileMetadataResponse).isEmpty();
    }

    @Test
    void givenAnEmptyDirectory_whenGettingFiles_thenReturnEmptyMetadata() throws IOException {
        Files.createDirectories(tempDir.resolve(USERNAME).resolve(SAMPLE_VIDEO_ID));

        List<FileMetadataResponse> fileMetadataResponse = mediaLibraryService.getFiles();

        assertThat(fileMetadataResponse).isEmpty();
    }

    @Test
    void givenStoredFile_whenGettingFiles_thenReturnFilesMetadata() throws IOException {
        Files.createDirectories(tempDir.resolve(USERNAME));
        Resource resource = new ClassPathResource(SAMPLE_VIDEO_ID);
        Path target = tempDir.resolve(USERNAME).resolve(SAMPLE_VIDEO_ID);
        Files.copy(resource.getInputStream(), target);

        List<FileMetadataResponse> fileMetadataResponse = mediaLibraryService.getFiles();

        assertThat(fileMetadataResponse).containsExactly(
                FileMetadataResponse.builder()
                        .id(SAMPLE_VIDEO_ID)
                        .name(SAMPLE_VIDEO_NAME)
                        .extension(SAMPLE_VIDEO_EXTENSION)
                        .size(SAMPLE_VIDEO_SIZE)
                        .build()
        );
    }

    @Test
    void givenViolation_whenGettingFile_thenThrowValidationException() {
        when(getFileValidator.validate(any(), any())).thenReturn(of(new Violation(VIOLATION_MESSAGE)));

        assertThrows(ValidationException.class,
                () -> mediaLibraryService.getFile(SAMPLE_VIDEO_ID)
        );
    }

    @Test
    void givenStoredFile_whenGettingFile_thenReturnFileMetadata() throws IOException {
        Files.createDirectories(tempDir.resolve(USERNAME));
        Resource resource = new ClassPathResource(SAMPLE_VIDEO_ID);
        Path target = tempDir.resolve(USERNAME).resolve(SAMPLE_VIDEO_ID);
        Files.copy(resource.getInputStream(), target);

        FileMetadataDetailedResponse fileMetadataDetailedResponse = mediaLibraryService.getFile(SAMPLE_VIDEO_ID);

        assertThat(fileMetadataDetailedResponse)
                .isEqualTo(
                        FileMetadataDetailedResponse.builder()
                                .id(SAMPLE_VIDEO_ID)
                                .name(SAMPLE_VIDEO_NAME)
                                .extension(SAMPLE_VIDEO_EXTENSION)
                                .size(SAMPLE_VIDEO_SIZE)
                                .mediaInformation(
                                        MediaInformation.builder()
                                                .generalInfo(getGeneralInfo())
                                                .videoInfo(getVideoInfo())
                                                .audioInfo(getAudioInfo())
                                                .build()
                                )
                                .build()
                );
    }

    @Test
    void givenViolation_whenServingFile_thenThrowValidationException() {
        when(getFileValidator.validate(any(), any())).thenReturn(of(new Violation(VIOLATION_MESSAGE)));

        assertThrows(ValidationException.class,
                () -> mediaLibraryService.serveVideo(SAMPLE_VIDEO_ID)
        );
    }

    @Test
    void givenStoredFile_whenServingFile_thenReturnFileResource() throws IOException {
        Files.createDirectories(tempDir.resolve(USERNAME));
        Resource resource = new ClassPathResource(SAMPLE_VIDEO_ID);
        Path target = tempDir.resolve(USERNAME).resolve(SAMPLE_VIDEO_ID);
        Files.copy(resource.getInputStream(), target);

        VideoServingResponse videoServingResponse = mediaLibraryService.serveVideo(SAMPLE_VIDEO_ID);

        assertThat(videoServingResponse).isEqualTo(new VideoServingResponse(new UrlResource(target.toUri()), SAMPLE_VIDEO_MIME));
    }

    @Test
    void givenViolation_whenUploadingFile_thenThrowValidationException() {
        when(addFileValidator.validate(any(), any())).thenReturn(of(new Violation(VIOLATION_MESSAGE)));

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn(SAMPLE_VIDEO_ID);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn(SAMPLE_VIDEO_MIME);

        UploadFileRequest uploadFileRequest = new UploadFileRequest();
        uploadFileRequest.setFile(multipartFile);

        assertThrows(ValidationException.class,
                () -> mediaLibraryService.addFile(uploadFileRequest)
        );
    }

    @Test
    void givenFileUploadRequest_whenUploadingFile_thenPersistFile() throws IOException {
        Files.createDirectories(tempDir.resolve(USERNAME));
        Path target = tempDir.resolve(USERNAME).resolve(SAMPLE_VIDEO_ID);

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn(SAMPLE_VIDEO_ID);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn(SAMPLE_VIDEO_MIME);

        UploadFileRequest uploadFileRequest = new UploadFileRequest();
        uploadFileRequest.setFile(multipartFile);
        mediaLibraryService.addFile(uploadFileRequest);

        verify(multipartFile).transferTo(new File(target.toFile().getPath()));
    }

    @Test
    void givenViolation_whenRenamingFile_thenThrowValidationException() {
        when(renameFileValidator.validate(any(), any())).thenReturn(of(new Violation(VIOLATION_MESSAGE)));

        assertThrows(ValidationException.class,
                () -> mediaLibraryService.renameFile(SAMPLE_VIDEO_ID, "new-sample-video.mp4")
        );
    }

    @Test
    void givenFileIdAndNewFilename_whenRenamingFile_thenRenameFile() throws IOException {
        Files.createDirectories(tempDir.resolve(USERNAME));
        Resource resource = new ClassPathResource(SAMPLE_VIDEO_ID);
        Path target = tempDir.resolve(USERNAME).resolve(SAMPLE_VIDEO_ID);
        Path targetNew = tempDir.resolve(USERNAME).resolve("new-sample-video.mp4");
        Files.copy(resource.getInputStream(), target);

        mediaLibraryService.renameFile(SAMPLE_VIDEO_ID, "new-sample-video.mp4");

        assertFalse(Files.exists(target));
        assertTrue(Files.exists(targetNew));
    }

    @Test
    void givenViolation_whenDeletingFile_thenThrowValidationException() {
        when(deleteFileValidator.validate(any(), any())).thenReturn(of(new Violation(VIOLATION_MESSAGE)));

        assertThrows(ValidationException.class,
                () -> mediaLibraryService.deleteFile(SAMPLE_VIDEO_ID)
        );
    }

    @Test
    void givenFileId_whenDeletingFile_thenDeleteFile() throws IOException {
        Files.createDirectories(tempDir.resolve(USERNAME));
        Resource resource = new ClassPathResource(SAMPLE_VIDEO_ID);
        Path target = tempDir.resolve(USERNAME).resolve(SAMPLE_VIDEO_ID);
        Files.copy(resource.getInputStream(), target);

        mediaLibraryService.deleteFile(SAMPLE_VIDEO_ID);

        assertFalse(Files.exists(target));
    }

    private void setupSecurity() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(new User(USERNAME, PASSWORD, of(new SimpleGrantedAuthority(ROLE))), PASSWORD);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    private MediaInformation.GeneralInfo getGeneralInfo() {
        return MediaInformation.GeneralInfo.builder()
                .duration(Duration.parse("P01DT02H03M04S"))
                .creationDate(new Date(0L))
                .modificationDate(new Date(0L))
                .totalBitrate("1 234 kb/s")
                .isStreamable(true)
                .build();
    }

    private MediaInformation.VideoInfo getVideoInfo() {
        return MediaInformation.VideoInfo.builder()
                .bitDepth("16 bits")
                .bitRate("789 kb/s")
                .codec("AV1")
                .encoder("H.256")
                .aspectRatio("16:9")
                .frames(3586)
                .framesPerSecond(59.5f)
                .width(1920)
                .height(1080)
                .build();
    }

    private MediaInformation.AudioInfo getAudioInfo() {
        return MediaInformation.AudioInfo.builder()
                .bitRate("321 kb/s")
                .bitRateMode("Constant")
                .numberOfChannels(2)
                .codec("AAC")
                .compressionMode("Lossy")
                .samplingRate("48.0 kHz")
                .build();
    }
}
