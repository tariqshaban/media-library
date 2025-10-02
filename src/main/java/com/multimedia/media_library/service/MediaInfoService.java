package com.multimedia.media_library.service;

import com.multimedia.media_library.model.MediaInformation;
import org.springframework.stereotype.Service;
import uk.co.caprica.vlcjinfo.MediaInfo;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

@Service
public class MediaInfoService {
    MediaInformation getMediaInfo(String mediaPath) {
        MediaInfo mediaInfo = MediaInfo.mediaInfo(mediaPath);
        return MediaInformation.builder()
                .generalInfo(getGeneralInfo(mediaInfo))
                .videoInfo(getVideoInfo(mediaInfo))
                .audioInfo(getAudioInfo(mediaInfo))
                .build();
    }

    private MediaInformation.GeneralInfo getGeneralInfo(MediaInfo mediaInfo) {
        Map<String, String> values = mediaInfo.sections().get("General").sections().get(0).values();

        return MediaInformation.GeneralInfo.builder()
                .duration(parseDuration(values.get("Duration")))
                .creationDate(parseDate(values.get("File creation date")))
                .modificationDate(parseDate(values.get("File last modification date")))
                .totalBitrate(values.get("Overall bit rate"))
                .isStreamable(parseBoolean(values.get("IsStreamable")))
                .build();
    }

    private MediaInformation.VideoInfo getVideoInfo(MediaInfo mediaInfo) {
        Map<String, String> values = mediaInfo.sections().get("Video").sections().get(0).values();

        return MediaInformation.VideoInfo.builder()
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

    private MediaInformation.AudioInfo getAudioInfo(MediaInfo mediaInfo) {
        Map<String, String> values = mediaInfo.sections().get("Audio").sections().get(0).values();

        return MediaInformation.AudioInfo.builder()
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
