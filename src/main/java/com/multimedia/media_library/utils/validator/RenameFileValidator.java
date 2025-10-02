package com.multimedia.media_library.utils.validator;

import com.multimedia.media_library.model.RenameFileRequest;
import com.multimedia.media_library.model.Violation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class RenameFileValidator implements Validator<RenameFileRequest> {
    @Value("${com.multimedia.media-library.media.path}")
    private String mediaPath;

    @Override
    public List<Violation> validate(RenameFileRequest renameFileRequest) {
        List<Violation> violations = new ArrayList<>();

        Path oldPath = Paths.get(mediaPath).resolve(renameFileRequest.getOldFilename());
        Path newPath = Paths.get(mediaPath).resolve(renameFileRequest.getNewFilename());

        if (!Files.exists(oldPath)) {
            violations.add(new Violation(String.format("File named \"%s\" cannot be deleted since it does not exist.", renameFileRequest.getOldFilename())));
        }
        if (Files.exists(newPath)) {
            violations.add(new Violation(String.format("File named \"%s\" already exist.", renameFileRequest.getNewFilename())));
        }

        return violations;
    }
}
