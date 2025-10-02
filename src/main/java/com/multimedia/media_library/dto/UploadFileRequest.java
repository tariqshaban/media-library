package com.multimedia.media_library.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadFileRequest {
    @NotNull(message = "A file must be provided")
    private MultipartFile file;
}
