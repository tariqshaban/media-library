package com.multimedia.media_library.utils;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

@Component
public class FileSize {
    public String formatSize(long bytes) {
        return FileUtils.byteCountToDisplaySize(bytes);
    }
}
