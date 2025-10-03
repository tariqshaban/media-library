package com.multimedia.media_library.service;

import com.multimedia.media_library.model.MediaInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.time.temporal.ChronoUnit.NANOS;
import static org.junit.jupiter.api.Assertions.*;

class MediaInfoServiceTest {
    MediaInfoService mediaInfoService;

    @BeforeEach
    void setUp() {
        mediaInfoService = new MediaInfoService();
    }

    @Test
    void givenFilePath_whenGetMediaInfo_thenReturnMediaInformation() {
        MediaInformation mediaInformation = mediaInfoService.getMediaInfo("src/test/resources/sample-video.mp4");

        assertEquals(376, mediaInformation.getGeneralInfo().getDuration().get(NANOS) / 1_000_000);
        assertNotNull(mediaInformation.getGeneralInfo().getCreationDate());
        assertNotNull(mediaInformation.getGeneralInfo().getModificationDate());
        assertEquals("3 568 kb/s", mediaInformation.getGeneralInfo().getTotalBitrate());
        assertTrue(mediaInformation.getGeneralInfo().isStreamable());

        assertEquals("8 bits", mediaInformation.getVideoInfo().getBitDepth());
        assertEquals("796 kb/s", mediaInformation.getVideoInfo().getBitRate());
        assertEquals("AV1", mediaInformation.getVideoInfo().getCodec());
        assertNull(mediaInformation.getVideoInfo().getEncoder());
        assertEquals("16:9", mediaInformation.getVideoInfo().getAspectRatio());
        assertEquals(132L, mediaInformation.getVideoInfo().getFrames());
        assertEquals(25.0, mediaInformation.getVideoInfo().getFramesPerSecond());
        assertEquals(1280, mediaInformation.getVideoInfo().getWidth());
        assertEquals(720, mediaInformation.getVideoInfo().getHeight());

        assertEquals("384 kb/s", mediaInformation.getAudioInfo().getBitRate());
        assertEquals("Constant", mediaInformation.getAudioInfo().getBitRateMode());
        assertEquals(6, mediaInformation.getAudioInfo().getNumberOfChannels());
        assertEquals("AAC", mediaInformation.getAudioInfo().getCodec());
        assertEquals("Lossy", mediaInformation.getAudioInfo().getCompressionMode());
        assertEquals("48.0 kHz", mediaInformation.getAudioInfo().getSamplingRate());
    }
}
