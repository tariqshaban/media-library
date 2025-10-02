package com.multimedia.media_library.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RenameFileRequest {
    String oldFilename;
    String newFilename;
}
