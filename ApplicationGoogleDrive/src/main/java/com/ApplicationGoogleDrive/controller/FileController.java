package com.ApplicationGoogleDrive.controller;


import com.ApplicationGoogleDrive.model.File;
import com.ApplicationGoogleDrive.model.User;
import com.ApplicationGoogleDrive.service.FileService;
import com.ApplicationGoogleDrive.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    private final Path rootLocation = Paths.get("uploads");

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isPublic", defaultValue = "false") boolean isPublic,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            User currentUser = userService.findByEmail(userDetails.getUsername());
            File savedFile = fileService.saveFile(file, description, isPublic, currentUser);
            return ResponseEntity.ok(savedFile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not upload the file: " + e.getMessage());
        }
    }

    @GetMapping("/my-files")
    public ResponseEntity<List<File>> getMyFiles(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        List<File> files = fileService.getFilesByOwner(currentUser);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/public")
    public ResponseEntity<List<File>> getPublicFiles() {
        List<File> files = fileService.getPublicFiles();
        return ResponseEntity.ok(files);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User currentUser = userService.findByEmail(userDetails.getUsername());
            File file = fileService.getFileWithAccessCheck(id, currentUser);

            Path path = rootLocation.resolve(file.getFilePath());
            byte[] fileContent = Files.readAllBytes(path);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", file.getFileName());
            headers.setContentLength(fileContent.length);

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        fileService.deleteFile(id, currentUser);
        return ResponseEntity.ok("File deleted successfully");
    }
}