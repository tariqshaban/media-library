package com.multimedia.media_library.utils.file_validator;

import com.multimedia.media_library.model.RenameFileRequest;
import com.multimedia.media_library.model.Violation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class RenameFileValidatorTest {
    @TempDir
    private Path tempDir;

    private RenameFileValidator renameFileValidator;

    @BeforeEach
    void setUp() {
        renameFileValidator = new RenameFileValidator();
    }

    @Test
    void givenNonExistingSourceAndNonExistingDestinationFiles_whenValidating_thenReturnViolation() {
        String sourceFilename = "NON_EXISTING_SOURCE_FILE";
        String destinationFilename = "NON_EXISTING_DESTINATION_FILE";

        RenameFileRequest renameFileRequest = new RenameFileRequest(sourceFilename, destinationFilename);
        List<Violation> validate = renameFileValidator.validate(tempDir.normalize().toString(), renameFileRequest);

        assertThat(validate)
                .map(Violation::getMessage)
                .containsExactly("File named \"NON_EXISTING_SOURCE_FILE\" cannot be renamed since it does not exist.");
    }

    @Test
    void givenNonExistingSourceAndExistingDestinationFiles_whenValidating_thenReturnViolation() throws IOException {
        String sourceFilename = "NON_EXISTING_SOURCE_FILE";
        String destinationFilename = "EXISTING_DESTINATION_FILE";
        Files.createFile(tempDir.resolve(destinationFilename));

        RenameFileRequest renameFileRequest = new RenameFileRequest(sourceFilename, destinationFilename);
        List<Violation> validate = renameFileValidator.validate(tempDir.normalize().toString(), renameFileRequest);

        assertThat(validate)
                .map(Violation::getMessage)
                .containsExactly(
                        "File named \"NON_EXISTING_SOURCE_FILE\" cannot be renamed since it does not exist.",
                        "File named \"EXISTING_DESTINATION_FILE\" already exist."
                );
    }

    @Test
    void givenExistingSourceAndNonExistingDestinationFiles_whenValidating_thenReturnViolation() throws IOException {
        String sourceFilename = "EXISTING_SOURCE_FILE";
        String destinationFilename = "NON_EXISTING_DESTINATION_FILE";
        Files.createFile(tempDir.resolve(sourceFilename));

        RenameFileRequest renameFileRequest = new RenameFileRequest(sourceFilename, destinationFilename);
        List<Violation> validate = renameFileValidator.validate(tempDir.normalize().toString(), renameFileRequest);

        assertThat(validate).isEmpty();
    }

    @Test
    void givenExistingSourceAndExistingDestinationFiles_whenValidating_thenReturnNoViolations() throws IOException {
        String sourceFilename = "EXISTING_SOURCE_FILE";
        String destinationFilename = "EXISTING_DESTINATION_FILE";
        Files.createFile(tempDir.resolve(sourceFilename));
        Files.createFile(tempDir.resolve(destinationFilename));

        RenameFileRequest renameFileRequest = new RenameFileRequest(sourceFilename, destinationFilename);
        List<Violation> validate = renameFileValidator.validate(tempDir.normalize().toString(), renameFileRequest);

        assertThat(validate)
                .map(Violation::getMessage)
                .containsExactly("File named \"EXISTING_DESTINATION_FILE\" already exist.");
    }
}
