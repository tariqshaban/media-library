package com.multimedia.media_library.mapper;

import com.multimedia.media_library.dto.FileMetadataDetailedResponse;
import com.multimedia.media_library.model.FileInfo;
import com.multimedia.media_library.model.MediaInformation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMetadataDetailedMapper {

    FileMetadataDetailedResponse toFileMetadataResponse(FileInfo fileInfo, MediaInformation mediaInformation);
}
