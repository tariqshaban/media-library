package com.multimedia.media_library.utils.file_validator;

import com.multimedia.media_library.model.Violation;

import java.util.List;

public interface FileValidator<T> {
    List<Violation> validate(String directory, T object);
}
