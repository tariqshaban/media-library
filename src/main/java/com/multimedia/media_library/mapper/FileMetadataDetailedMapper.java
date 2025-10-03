package com.multimedia.media_library.mapper;

import com.multimedia.media_library.dto.FileMetadataDetailedResponse;
import com.multimedia.media_library.model.FileInformation;
import com.multimedia.media_library.model.MediaInformation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMetadataDetailedMapper {

    FileMetadataDetailedResponse toFileMetadataResponse(FileInformation fileInformation, MediaInformation mediaInformation);
}
