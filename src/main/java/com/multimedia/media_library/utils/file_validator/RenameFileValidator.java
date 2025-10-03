package com.multimedia.media_library.utils.file_validator;

import com.multimedia.media_library.model.RenameFileRequest;
import com.multimedia.media_library.model.Violation;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class RenameFileValidator implements FileValidator<RenameFileRequest> {
    @Override
    public List<Violation> validate(String directory, RenameFileRequest renameFileRequest) {
        List<Violation> violations = new ArrayList<>();

        Path oldPath = Paths.get(directory).resolve(renameFileRequest.getOldFilename());
        Path newPath = Paths.get(directory).resolve(renameFileRequest.getNewFilename());

        if (!Files.exists(oldPath)) {
            violations.add(new Violation(String.format("File named \"%s\" cannot be renamed since it does not exist.", renameFileRequest.getOldFilename())));
        }
        if (Files.exists(newPath)) {
            violations.add(new Violation(String.format("File named \"%s\" already exist.", renameFileRequest.getNewFilename())));
        }

        return violations;
    }
}
