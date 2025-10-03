package com.multimedia.media_library.utils.file_validator;

import com.multimedia.media_library.model.Violation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.util.List.of;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AddFileValidatorTest {
    @TempDir
    private Path tempDir;
    private MultipartFile multipartFile;

    private AddFileValidator addFileValidator;

    @BeforeEach
    void setUp() {
        multipartFile = mock(MultipartFile.class);
        List<String> allowedExtensions = of("mp4", "mkv", "avi");
        addFileValidator = new AddFileValidator(allowedExtensions);
    }

    @Test
    void givenEmptyFile_whenValidating_thenReturnViolation() {
        when(multipartFile.isEmpty()).thenReturn(true);

        List<Violation> validate = addFileValidator.validate(tempDir.normalize().toString(), multipartFile);

        assertThat(validate)
                .map(Violation::getMessage)
                .containsExactly(String.format("Invalid file named \"%s\".", multipartFile.getOriginalFilename()));
    }

    @Test
    void givenEmptyFilename_whenValidating_thenReturnViolation() {
        when(multipartFile.getOriginalFilename()).thenReturn(null);

        List<Violation> validate = addFileValidator.validate(tempDir.normalize().toString(), multipartFile);

        assertThat(validate)
                .map(Violation::getMessage)
                .containsExactly(
                        String.format("File named \"%s\" can only be a video mime type.", multipartFile.getOriginalFilename()),
                        String.format("File named \"%s\" does not have an allowed file extension.", multipartFile.getOriginalFilename())
                );
    }

    @ParameterizedTest
    @CsvSource({
            ",",
            "text/plain,txt",
            "text/csv,csv",
            "image/jpeg,jpg",
            "application/pdf,pdf",
            "application/json,json",
            "audio/mp3,mp3",
    })
    void givenInvalidContentType_whenValidating_thenReturnViolation(String contentType, String extension) {
        when(multipartFile.getOriginalFilename()).thenReturn("FILE." + extension);
        when(multipartFile.getContentType()).thenReturn(contentType);

        List<Violation> validate = addFileValidator.validate(tempDir.normalize().toString(), multipartFile);

        assertThat(validate)
                .map(Violation::getMessage)
                .containsExactly(
                        String.format("File named \"%s\" can only be a video mime type.", multipartFile.getOriginalFilename()),
                        String.format("File named \"%s\" does not have an allowed file extension.", multipartFile.getOriginalFilename())
                );
    }

    @ParameterizedTest
    @CsvSource({
            "video/webm,webm",
            "video/mov,mov",
            "video/m4v,m4v",
    })
    void givenNotAllowedContentType_whenValidating_thenReturnViolation(String contentType, String extension) {
        when(multipartFile.getOriginalFilename()).thenReturn("FILE." + extension);
        when(multipartFile.getContentType()).thenReturn(contentType);

        List<Violation> validate = addFileValidator.validate(tempDir.normalize().toString(), multipartFile);

        assertThat(validate)
                .map(Violation::getMessage)
                .containsExactly(
                        String.format("File named \"%s\" does not have an allowed file extension.", multipartFile.getOriginalFilename())
                );
    }

    @Test
    void givenExistingFile_whenValidating_thenReturnViolation() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn("FILE.mp4");
        when(multipartFile.getContentType()).thenReturn("video/mp4");

        String sourceFilename = "FILE.mp4";
        Files.createFile(tempDir.resolve(sourceFilename));

        List<Violation> validate = addFileValidator.validate(tempDir.normalize().toString(), multipartFile);

        assertThat(validate)
                .map(Violation::getMessage)
                .containsExactly(String.format("File named \"%s\" already exist.", multipartFile.getOriginalFilename()));
    }

    @Test
    void givenNewFile_whenValidating_thenReturnNoViolations() {
        when(multipartFile.getOriginalFilename()).thenReturn("FILE.mp4");
        when(multipartFile.getContentType()).thenReturn("video/mp4");

        List<Violation> validate = addFileValidator.validate(tempDir.normalize().toString(), multipartFile);

        assertThat(validate).isEmpty();
    }
}
