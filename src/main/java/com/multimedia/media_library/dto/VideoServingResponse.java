package com.multimedia.media_library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.io.Resource;

@Data
@AllArgsConstructor
public class VideoServingResponse {
    private Resource resource;
    private String mimeType;
}
