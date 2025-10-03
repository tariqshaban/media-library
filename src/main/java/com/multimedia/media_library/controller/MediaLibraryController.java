package com.multimedia.media_library.controller;

import com.multimedia.media_library.dto.UploadFileRequest;
import com.multimedia.media_library.dto.VideoServingResponse;
import com.multimedia.media_library.service.MediaLibraryService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.multimedia.media_library.common.Constants.REDIRECT_PATH;
import static com.multimedia.media_library.common.Constants.SUCCESS_MESSAGE_KEY;

@Controller
@Validated
class MediaLibraryController implements RedirectAttributeProvider {
    private final MediaLibraryService mediaLibraryService;
    private final String allowedExtensions;

    private String allowedExtensionsTransformed;

    MediaLibraryController(MediaLibraryService mediaLibraryService, @Value("${com.multimedia.media-library.media.allowed.extensions}") String allowedExtensions) {
        this.mediaLibraryService = mediaLibraryService;
        this.allowedExtensions = allowedExtensions;
    }

    @PostConstruct
    void init() {
        allowedExtensionsTransformed = Arrays.stream(allowedExtensions.split(","))
                .map(ext -> "." + ext)
                .collect(Collectors.joining(","));
    }

    @GetMapping("/")
    String index(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("allowedExtensions", allowedExtensionsTransformed);
        model.addAttribute("uploadFileRequest", new UploadFileRequest());
        model.addAttribute("fileMetadataResponse", mediaLibraryService.getFiles());
        return "index";
    }

    @GetMapping("/get-file")
    String getFile(
            @RequestParam @NotBlank(message = "An ID must be provided") String id,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("fileMetadataDetailedResponse", mediaLibraryService.getFile(id));
        addRedirectAttributes(redirectAttributes);

        return REDIRECT_PATH;
    }

    @GetMapping("/serve-video/{id}")
    ResponseEntity<Resource> serveVideo(@PathVariable String id) {
        VideoServingResponse videoServingResponse = mediaLibraryService.serveVideo(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(videoServingResponse.getMimeType()))
                .body(videoServingResponse.getResource());
    }

    @PostMapping("/upload-file")
    String uploadFile(
            @ModelAttribute @Valid UploadFileRequest uploadFileRequest,
            RedirectAttributes redirectAttributes
    ) {
        mediaLibraryService.addFile(uploadFileRequest);
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE_KEY, "File uploaded successfully.");
        addRedirectAttributes(redirectAttributes);

        return REDIRECT_PATH;
    }

    @PostMapping("/rename-file")
    String renameFile(
            @RequestParam @NotBlank(message = "An ID must be provided") String id,
            @RequestParam @NotBlank(message = "A new filename must be provided") String newFilename,
            RedirectAttributes redirectAttributes
    ) {
        mediaLibraryService.renameFile(id, newFilename);
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE_KEY, "File renamed successfully.");
        addRedirectAttributes(redirectAttributes);

        return REDIRECT_PATH;
    }

    @PostMapping("/delete-file")
    String deleteFile(
            @RequestParam @NotBlank(message = "An ID must be provided") String id,
            RedirectAttributes redirectAttributes
    ) {
        mediaLibraryService.deleteFile(id);
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE_KEY, "File deleted successfully.");
        addRedirectAttributes(redirectAttributes);

        return REDIRECT_PATH;
    }

    @Override
    public void addRedirectAttributes(RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        redirectAttributes.addFlashAttribute("username", ((UserDetails) auth.getPrincipal()).getUsername());
        redirectAttributes.addFlashAttribute("allowedExtensions", allowedExtensionsTransformed);
        redirectAttributes.addFlashAttribute("uploadFileRequest", new UploadFileRequest());
        redirectAttributes.addFlashAttribute("fileMetadataResponse", mediaLibraryService.getFiles());
    }
}
