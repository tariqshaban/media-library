package com.multimedia.media_library.mapper;

import com.multimedia.media_library.dto.FileMetadataResponse;
import com.multimedia.media_library.model.FileInformation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMetadataMapper {

    FileMetadataResponse toFileMetadataResponse(FileInformation fileInformation);
}
