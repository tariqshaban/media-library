package com.multimedia.media_library.utils.validator;

import com.multimedia.media_library.model.Violation;

import java.util.List;

public interface Validator<T> {
    List<Violation> validate(T object);
}
