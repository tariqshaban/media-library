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
import com.multimedia.media_library.utils.file_validator.AddFileValidator;
import com.multimedia.media_library.utils.file_validator.DeleteFileValidator;
import com.multimedia.media_library.utils.file_validator.GetFileValidator;
import com.multimedia.media_library.utils.file_validator.RenameFileValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final FileInfoService fileInfoService;
    private final MediaInfoService mediaInfoService;
    private final GetFileValidator getFileValidator;
    private final AddFileValidator addFileValidator;
    private final RenameFileValidator renameFileValidator;
    private final DeleteFileValidator deleteFileValidator;
    private final FileMetadataMapper fileMetadataMapper;
    private final FileMetadataDetailedMapper fileMetadataDetailedMapper;
    private final String mediaPath;

    public MediaLibraryService(FileInfoService fileInfoService, MediaInfoService mediaInfoService, GetFileValidator getFileValidator, AddFileValidator addFileValidator, RenameFileValidator renameFileValidator, DeleteFileValidator deleteFileValidator, FileMetadataMapper fileMetadataMapper, FileMetadataDetailedMapper fileMetadataDetailedMapper, @Value("${com.multimedia.media-library.media.path}") String mediaPath) {
        this.fileInfoService = fileInfoService;
        this.mediaInfoService = mediaInfoService;
        this.getFileValidator = getFileValidator;
        this.addFileValidator = addFileValidator;
        this.renameFileValidator = renameFileValidator;
        this.deleteFileValidator = deleteFileValidator;
        this.fileMetadataMapper = fileMetadataMapper;
        this.fileMetadataDetailedMapper = fileMetadataDetailedMapper;
        this.mediaPath = mediaPath;
    }

    public List<FileMetadataResponse> getFiles() {
        String userMediaPath = getUserMediaPath();
        File folder = new File(userMediaPath);
        return Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .filter(file -> !file.isDirectory())
                .map(File::getPath)
                .map(fileInfoService::getFileInfo)
                .map(fileMetadataMapper::toFileMetadataResponse)
                .toList();
    }

    public FileMetadataDetailedResponse getFile(String filename) {
        String userMediaPath = getUserMediaPath();
        List<Violation> violations = getFileValidator.validate(userMediaPath, filename);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }

        String filePath = userMediaPath +
                File.separator +
                filename;
        return fileMetadataDetailedMapper.toFileMetadataResponse(
                fileInfoService.getFileInfo(filePath),
                mediaInfoService.getMediaInfo(filePath)
        );
    }

    public VideoServingResponse serveVideo(String filename) {
        String userMediaPath = getUserMediaPath();
        try {
            VideoServingResponse videoServingResponse = new VideoServingResponse();

            Path videoPath = Paths.get(userMediaPath).resolve(filename);
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
        String userMediaPath = getUserMediaPath();
        try {
            List<Violation> violations = addFileValidator.validate(userMediaPath, uploadFileRequest.getFile());
            if (!violations.isEmpty()) {
                throw new ValidationException(violations);
            }

            String filePath = userMediaPath +
                    File.separator +
                    uploadFileRequest.getFile().getOriginalFilename();
            uploadFileRequest.getFile().transferTo(new File(filePath));
        } catch (IOException e) {
            throw new UnhandledException(String.format("Failed to persist file named \"%s\"", uploadFileRequest.getFile().getOriginalFilename()));
        }
    }

    public void renameFile(String id, String newFilename) {
        String userMediaPath = getUserMediaPath();
        try {
            List<Violation> violations = renameFileValidator.validate(userMediaPath, new RenameFileRequest(id, newFilename));
            if (!violations.isEmpty()) {
                throw new ValidationException(violations);
            }

            Path basePath = Paths.get(userMediaPath);
            Path path = basePath.resolve(id);
            Path pathNewFilename = basePath.resolve(newFilename);
            Files.move(path, pathNewFilename);
        } catch (IOException e) {
            throw new UnhandledException(String.format("Failed to rename file named \"%s\" to \"%s\"", id, newFilename));
        }
    }

    public void deleteFile(String filename) {
        String userMediaPath = getUserMediaPath();
        try {
            List<Violation> violations = deleteFileValidator.validate(userMediaPath, filename);
            if (!violations.isEmpty()) {
                throw new ValidationException(violations);
            }

            Path path = Paths.get(userMediaPath).resolve(filename);
            Files.delete(path);
        } catch (IOException e) {
            throw new UnhandledException(String.format("Failed to delete file named \"%s\"", filename));
        }
    }

    private String getUserMediaPath() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return mediaPath + File.separator + ((UserDetails) auth.getPrincipal()).getUsername();
    }
}
