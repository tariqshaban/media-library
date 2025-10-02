package com.multimedia.media_library.utils.file_validator;

import com.multimedia.media_library.model.Violation;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class GetFileValidator implements FileValidator<String> {
    @Override
    public List<Violation> validate(String directory, String filename) {
        List<Violation> violations = new ArrayList<>();

        Path path = Paths.get(directory).resolve(filename);

        if (!Files.exists(path)) {
            violations.add(new Violation(String.format("File named \"%s\" cannot be retrieved since it does not exist.", filename)));
        }

        return violations;
    }
}
