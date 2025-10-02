package com.multimedia.media_library.mapper;

import com.multimedia.media_library.dto.FileMetadataDetailedResponse;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileMetadataDetailedMapper {
    public FileMetadataDetailedResponse fileToFileMetadataResponse(File file) {
        return FileMetadataDetailedResponse.builder()
                .id(getId(file))
                .name(getName(file))
                .extension(getExtension(file))
                .size(getSize(file))
                .build();
    }

    private String getId(File file) {
        return file.getName();
    }

    private String getName(File file) {
        String fullName = file.getName();
        return fullName.substring(0, fullName.lastIndexOf('.'));
    }

    private String getExtension(File file) {
        String fullName = file.getName();
        return fullName.substring(fullName.lastIndexOf('.') + 1);
    }

    private long getSize(File file) {
        return file.length();
    }
}
