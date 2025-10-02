package com.multimedia.media_library.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileMetadataDetailedResponse {
    private String id;
    private String name;
    private String extension;
    private long size;
}
