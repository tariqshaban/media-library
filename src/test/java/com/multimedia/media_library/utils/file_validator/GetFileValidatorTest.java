package com.multimedia.media_library.utils.file_validator;

import com.multimedia.media_library.model.Violation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class GetFileValidatorTest {
    @TempDir
    private Path tempDir;

    private GetFileValidator getFileValidator;

    @BeforeEach
    void setUp() {
        getFileValidator = new GetFileValidator();
    }

    @Test
    void givenNonExistingFile_whenValidating_thenReturnViolation() {
        String filename = "NON_EXISTING_FILE";

        List<Violation> validate = getFileValidator.validate(tempDir.normalize().toString(), filename);

        assertThat(validate)
                .map(Violation::getMessage)
                .containsExactly("File named \"NON_EXISTING_FILE\" cannot be retrieved since it does not exist.");
    }

    @Test
    void givenExistingFile_whenValidating_thenReturnNoViolations() throws IOException {
        String filename = "EXISTING_FILE";
        Files.createFile(tempDir.resolve(filename));

        List<Violation> validate = getFileValidator.validate(tempDir.normalize().toString(), filename);

        assertThat(validate).isEmpty();
    }
}
