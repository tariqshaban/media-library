package com.multimedia.media_library.component;

import com.multimedia.media_library.service.FileUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ApplicationStartupTest {
    @TempDir
    private Path tempDir;
    private FileUserDetailsService fileUserDetailsService;

    private ApplicationStartup applicationStartup;

    @BeforeEach
    void setUp() {
        fileUserDetailsService = mock(FileUserDetailsService.class);
        applicationStartup = new ApplicationStartup(fileUserDetailsService, tempDir.toString());
    }

    @Test
    void givenUsers_whenStartingUp_thenCreateDirectoriesForUsers() throws IOException {
        when(fileUserDetailsService.getUsers()).thenReturn(of("Alice", "Bob"));

        applicationStartup.onApplicationReady();

        verify(fileUserDetailsService).getUsers();
        assertTrue(Files.exists(tempDir) && Files.isDirectory(tempDir));
        assertTrue(Files.exists(tempDir.resolve("Alice")) && Files.isDirectory(tempDir.resolve("Bob")));
        assertTrue(Files.exists(tempDir.resolve("Bob")) && Files.isDirectory(tempDir.resolve("Alice")));
    }
}
