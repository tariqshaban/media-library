package com.multimedia.media_library.mapper;

import com.multimedia.media_library.dto.FileMetadataDetailedResponse;
import org.springframework.stereotype.Component;
import uk.co.caprica.vlcjinfo.MediaInfo;

import java.io.File;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

@Component
public class FileMetadataDetailedMapper {
    public FileMetadataDetailedResponse fileToFileMetadataResponse(File file) {
        MediaInfo mediaInfo = MediaInfo.mediaInfo(file.getAbsolutePath());
        return FileMetadataDetailedResponse.builder()
                .id(getId(file))
                .name(getName(file))
                .extension(getExtension(file))
                .size(getSize(file))
                .generalInfo(getGeneralInfo(mediaInfo))
                .videoInfo(getVideoInfo(mediaInfo))
                .audioInfo(getAudioInfo(mediaInfo))
                .build();
    }

    private String getId(File file) {
        return file.getName();
    }

    private String getName(File file) {
        String fullName = file.getName();
        return fullName.substring(0, fullName.lastIndexOf('.'));
    }

    private String getExtension(File file) {
        String fullName = file.getName();
        return fullName.substring(fullName.lastIndexOf('.') + 1);
    }

    private long getSize(File file) {
        return file.length();
    }

    private FileMetadataDetailedResponse.GeneralInfo getGeneralInfo(MediaInfo mediaInfo) {
        Map<String, String> values = mediaInfo.sections().get("General").sections().get(0).values();

        return FileMetadataDetailedResponse.GeneralInfo.builder()
                .duration(parseDuration(values.get("Duration")))
                .creationDate(parseDate(values.get("File creation date")))
                .modificationDate(parseDate(values.get("File last modification date")))
                .totalBitrate(values.get("Overall bit rate"))
                .isStreamable(parseBoolean(values.get("IsStreamable")))
                .build();
    }

    private FileMetadataDetailedResponse.VideoInfo getVideoInfo(MediaInfo mediaInfo) {
        Map<String, String> values = mediaInfo.sections().get("Video").sections().get(0).values();

        return FileMetadataDetailedResponse.VideoInfo.builder()
                .bitDepth(values.get("Bit depth"))
                .bitRate(values.get("Bit rate"))
                .codec(values.get("Commercial name"))
                .encoder(values.get("Internet media type"))
                .aspectRatio(values.get("Display aspect ratio"))
                .frames(parseInt(values.get("Frame count")))
                .framesPerSecond(parseFloat(values.get("Frame rate")))
                .width(parseInt(values.get("Width")))
                .height(parseInt(values.get("Height")))
                .build();
    }

    private FileMetadataDetailedResponse.AudioInfo getAudioInfo(MediaInfo mediaInfo) {
        Map<String, String> values = mediaInfo.sections().get("Audio").sections().get(0).values();

        return FileMetadataDetailedResponse.AudioInfo.builder()
                .bitRate(values.get("Bit rate"))
                .bitRateMode(values.get("Bit rate mode"))
                .numberOfChannels(parseInt(values.get("Channel(s)")))
                .codec(values.get("Commercial name"))
                .compressionMode(values.get("Compression mode"))
                .samplingRate(values.get("Sampling rate"))
                .build();
    }

    private static Duration parseDuration(String value) {
        String str = value.split(" ")[0];
        String[] hms = str.split(":");

        int hours = hms.length == 3 ? Integer.parseInt(hms[0]) : 0;
        int minutes = hms.length >= 2 ? Integer.parseInt(hms[hms.length - 2]) : 0;
        double seconds = Double.parseDouble(hms[hms.length - 1]);

        long totalMillis = (hours * 3600L + minutes * 60L) * 1000 + (long) (seconds * 1000);
        return Duration.ofMillis(totalMillis);
    }

    private static Date parseDate(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS z");
        return Date.from(ZonedDateTime.parse(value, formatter).toInstant()
        );
    }

    private static int parseInt(String value) {
        return Integer.parseInt(value.replaceAll("\\D", ""));
    }

    private static float parseFloat(String value) {
        return Float.parseFloat(value.replaceAll("[^\\d.]", ""));
    }

    private static boolean parseBoolean(String value) {
        return value.equalsIgnoreCase("yes");
    }
}
