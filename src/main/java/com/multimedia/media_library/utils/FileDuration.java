package com.multimedia.media_library.utils;

import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class FileDuration {
    public String formatDuration(Duration duration) {
        if (duration == null) return "";
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        return String.format(
                "%02d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60
        );
    }
}
