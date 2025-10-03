package com.multimedia.media_library.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileDurationTest {
    private FileDuration fileDuration;

    @BeforeEach
    void setUp() {
        fileDuration = new FileDuration();
    }

    @ParameterizedTest
    @CsvSource({
            "P0DT0H0M0S, 00:00:00",
            "P00DT00H00M00S, 00:00:00",
            "P0DT0H0M1S, 00:00:01",
            "P0DT0H1M0S, 00:01:00",
            "P0DT1H0M0S, 01:00:00",
            "P1DT0H0M0S, 24:00:00",
            "P1DT2H3M4S, 26:03:04",
            "P01DT02H03M04S, 26:03:04",
            "P11DT22H33M44S, 286:33:44",
    })
    void givenFileDuration_whenFormatDuration_thenReturnUserInterpretedDuration(String durationInput, String expectedFormattedDuration) {
        Duration duration = Duration.parse(durationInput);
        String formattedDuration = fileDuration.formatDuration(duration);

        assertEquals(expectedFormattedDuration, formattedDuration);
    }
}
