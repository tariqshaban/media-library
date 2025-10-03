package com.multimedia.media_library.component;

import com.multimedia.media_library.service.FileUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class ApplicationStartup {
    private final FileUserDetailsService fileUserDetailsService;
    private final String mediaPath;

    public ApplicationStartup(FileUserDetailsService fileUserDetailsService, @Value("${com.multimedia.media-library.media.path}") String mediaPath) {
        this.fileUserDetailsService = fileUserDetailsService;
        this.mediaPath = mediaPath;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() throws IOException {
        Files.createDirectories(Paths.get(mediaPath));
        for (String user : fileUserDetailsService.getUsers()) {
            Files.createDirectories(Paths.get(mediaPath).resolve(user));
        }
    }
}
