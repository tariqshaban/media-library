package com.multimedia.media_library.service;

import com.multimedia.media_library.dto.FileMetadataDetailedResponse;
import com.multimedia.media_library.dto.FileMetadataResponse;
import com.multimedia.media_library.dto.UploadFileRequest;
import com.multimedia.media_library.dto.VideoServingResponse;
import com.multimedia.media_library.exception.UnhandledException;
import com.multimedia.media_library.exception.ValidationException;
import com.multimedia.media_library.mapper.FileMetadataDetailedMapper;
import com.multimedia.media_library.mapper.FileMetadataMapper;
import com.multimedia.media_library.model.RenameFileRequest;
import com.multimedia.media_library.model.Violation;
import com.multimedia.media_library.utils.validator.AddFileValidator;
import com.multimedia.media_library.utils.validator.DeleteFileValidator;
import com.multimedia.media_library.utils.validator.GetFileValidator;
import com.multimedia.media_library.utils.validator.RenameFileValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class MediaLibraryService {
    private final GetFileValidator getFileValidator;
    private final AddFileValidator addFileValidator;
    private final RenameFileValidator renameFileValidator;
    private final DeleteFileValidator deleteFileValidator;
    private final FileMetadataMapper fileMetadataMapper;
    private final FileMetadataDetailedMapper fileMetadataDetailedMapper;
    @Value("${com.multimedia.media-library.media.path}")
    private String mediaPath;

    public MediaLibraryService(GetFileValidator getFileValidator, AddFileValidator addFileValidator, RenameFileValidator renameFileValidator, DeleteFileValidator deleteFileValidator, FileMetadataMapper fileMetadataMapper, FileMetadataDetailedMapper fileMetadataDetailedMapper) {
        this.getFileValidator = getFileValidator;
        this.addFileValidator = addFileValidator;
        this.renameFileValidator = renameFileValidator;
        this.deleteFileValidator = deleteFileValidator;
        this.fileMetadataMapper = fileMetadataMapper;
        this.fileMetadataDetailedMapper = fileMetadataDetailedMapper;
    }

    public List<FileMetadataResponse> getFiles() {
        File folder = new File(mediaPath);
        return Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .filter(file -> !file.isDirectory())
                .map(fileMetadataMapper::fileToFileMetadataResponse)
                .toList();
    }

    public FileMetadataDetailedResponse getFile(String filename) {
        List<Violation> violations = getFileValidator.validate(filename);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }

        String filePath = mediaPath +
                File.separator +
                filename;
        return fileMetadataDetailedMapper.fileToFileMetadataResponse(new File(filePath));
    }

    public VideoServingResponse serveVideo(String filename) {
        try {
            VideoServingResponse videoServingResponse = new VideoServingResponse();

            Path videoPath = Paths.get(mediaPath).resolve(filename);
            UrlResource resource = new UrlResource(videoPath.toUri());
            videoServingResponse.setResource(resource);

            String mimeType = Files.probeContentType(videoPath);
            videoServingResponse.setMimeType(mimeType);

            return videoServingResponse;
        } catch (IOException e) {
            throw new UnhandledException(String.format("Failed to serve file named \"%s\"", filename));
        }
    }

    public void addFile(UploadFileRequest uploadFileRequest) {
        try {
            List<Violation> violations = addFileValidator.validate(uploadFileRequest);
            if (!violations.isEmpty()) {
                throw new ValidationException(violations);
            }

            String filePath = mediaPath +
                    File.separator +
                    uploadFileRequest.getFile().getOriginalFilename();
            uploadFileRequest.getFile().transferTo(new File(filePath));
        } catch (IOException e) {
            throw new UnhandledException(String.format("Failed to persist file named \"%s\"", uploadFileRequest.getFile().getOriginalFilename()));
        }
    }

    public void renameFile(String id, String newFilename) {
        try {
            List<Violation> violations = renameFileValidator.validate(new RenameFileRequest(id, newFilename));
            if (!violations.isEmpty()) {
                throw new ValidationException(violations);
            }

            Path path = Paths.get(mediaPath).resolve(id);
            Path pathNewFilename = Paths.get(mediaPath).resolve(newFilename);
            Files.move(path, pathNewFilename);
        } catch (IOException e) {
            throw new UnhandledException(String.format("Failed to rename file named \"%s\" to \"%s\"", id, newFilename));
        }
    }

    public void deleteFile(String filename) {
        try {
            List<Violation> violations = deleteFileValidator.validate(filename);
            if (!violations.isEmpty()) {
                throw new ValidationException(violations);
            }

            Path path = Paths.get(mediaPath).resolve(filename);
            Files.delete(path);
        } catch (IOException e) {
            throw new UnhandledException(String.format("Failed to delete file named \"%s\"", filename));
        }
    }
}
