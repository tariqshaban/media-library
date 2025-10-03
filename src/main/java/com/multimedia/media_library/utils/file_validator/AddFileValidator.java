package com.multimedia.media_library.utils.file_validator;

import com.multimedia.media_library.model.Violation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class AddFileValidator implements FileValidator<MultipartFile> {
    private final List<String> allowedExtensions;

    public AddFileValidator(@Value("${com.multimedia.media-library.media.allowed.extensions}") List<String> allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }

    @Override
    public List<Violation> validate(String directory, MultipartFile file) {
        List<Violation> violations = new ArrayList<>();

        boolean isRequestIncomplete = file.isEmpty();
        if (isRequestIncomplete) {
            violations.add(new Violation(String.format("Invalid file named \"%s\".", file.getOriginalFilename())));
            return violations;
        }

        String contentType = Objects.requireNonNull(file).getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            violations.add(new Violation(String.format("File named \"%s\" can only be a video mime type.", file.getOriginalFilename())));
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !hasAllowedExtension(filename)) {
            violations.add(new Violation(String.format("File named \"%s\" does not have an allowed file extension.", file.getOriginalFilename())));
            return violations;
        }

        Path path = Paths.get(directory).resolve(filename);
        if (Files.exists(path)) {
            violations.add(new Violation(String.format("File named \"%s\" already exist.", filename)));
        }

        return violations;
    }

    private boolean hasAllowedExtension(String filename) {
        String extension = getFileExtension(filename);
        return allowedExtensions.contains(extension.toLowerCase());
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex != -1) ? filename.substring(dotIndex + 1) : "";
    }
}
