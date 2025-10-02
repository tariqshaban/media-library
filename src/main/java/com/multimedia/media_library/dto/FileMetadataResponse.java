package com.multimedia.media_library.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileMetadataResponse {
    private String id;
    private String name;
    private String extension;
    private long size;
}
