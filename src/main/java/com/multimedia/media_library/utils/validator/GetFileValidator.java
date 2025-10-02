package com.multimedia.media_library.utils.validator;

import com.multimedia.media_library.model.Violation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class GetFileValidator implements Validator<String> {
    @Value("${com.multimedia.media-library.media.path}")
    private String mediaPath;

    @Override
    public List<Violation> validate(String filename) {
        List<Violation> violations = new ArrayList<>();

        Path path = Paths.get(mediaPath).resolve(filename);

        if (!Files.exists(path)) {
            violations.add(new Violation(String.format("File named \"%s\" cannot be retrieved since it does not exist.", filename)));
        }

        return violations;
    }
}
