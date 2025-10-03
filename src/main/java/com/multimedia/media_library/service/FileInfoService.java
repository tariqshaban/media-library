package com.multimedia.media_library.service;

import com.multimedia.media_library.model.FileInformation;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FileInfoService {
    public FileInformation getFileInformation(String filePath) {
        File file = new File(filePath);
        return FileInformation.builder()
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
