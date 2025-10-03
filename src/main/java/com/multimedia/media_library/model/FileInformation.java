package com.multimedia.media_library.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileInformation {
    private String id;
    private String name;
    private String extension;
    private long size;
}
