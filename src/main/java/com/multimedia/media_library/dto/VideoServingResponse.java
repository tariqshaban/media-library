package com.multimedia.media_library.dto;

import lombok.Data;
import org.springframework.core.io.Resource;

@Data
public class VideoServingResponse {
    private Resource resource;
    private String mimeType;
}
