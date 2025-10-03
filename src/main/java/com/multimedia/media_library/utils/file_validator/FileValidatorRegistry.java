package com.multimedia.media_library.utils.file_validator;

import org.springframework.stereotype.Component;

import java.util.Map;

import static com.multimedia.media_library.utils.file_validator.FileValidatorOperationType.*;

@Component
public class FileValidatorRegistry {
    private final Map<FileValidatorOperationType, FileValidator<?>> validators;

    public FileValidatorRegistry(GetFileValidator getFileValidator, AddFileValidator addFileValidator, RenameFileValidator renameFileValidator, DeleteFileValidator deleteFileValidator) {
        this.validators = Map.of(
                GET, getFileValidator,
                ADD, addFileValidator,
                RENAME, renameFileValidator,
                DELETE, deleteFileValidator
        );
    }

    @SuppressWarnings("unchecked")
    public <T> FileValidator<T> get(FileValidatorOperationType type) {
        return (FileValidator<T>) validators.get(type);
    }
}
