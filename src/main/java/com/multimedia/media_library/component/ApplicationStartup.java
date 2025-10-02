package com.multimedia.media_library.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class ApplicationStartup {
    @Value("${com.multimedia.media-library.media.path}")
    private String mediaPath;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() throws IOException {
        Files.createDirectories(Paths.get(mediaPath));
    }
}
