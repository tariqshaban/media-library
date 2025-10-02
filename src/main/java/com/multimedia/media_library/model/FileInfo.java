package com.multimedia.media_library.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileInfo {
    private String id;
    private String name;
    private String extension;
    private long size;
}
