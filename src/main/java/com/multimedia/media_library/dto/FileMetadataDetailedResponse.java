package com.multimedia.media_library.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.util.Date;

@Data
@Builder
public class FileMetadataDetailedResponse {
    private String id;
    private String name;
    private String extension;
    private long size;
    private GeneralInfo generalInfo;
    private VideoInfo videoInfo;
    private AudioInfo audioInfo;

    @Data
    @Builder
    public static class GeneralInfo {
        Duration duration;
        Date creationDate;
        Date modificationDate;
        String totalBitrate;
        boolean isStreamable;
    }

    @Data
    @Builder
    public static class VideoInfo {
        String bitDepth;
        String bitRate;
        String codec;
        String encoder;
        String aspectRatio;
        int frames;
        float framesPerSecond;
        int width;
        int height;
    }

    @Data
    @Builder
    public static class AudioInfo {
        String bitRate;
        String bitRateMode;
        int numberOfChannels;
        String codec;
        String compressionMode;
        String samplingRate;
    }
}
