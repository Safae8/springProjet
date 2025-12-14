package com.ApplicationGoogleDrive.service;

import com.ApplicationGoogleDrive.model.File;
import com.ApplicationGoogleDrive.model.User;
import com.ApplicationGoogleDrive.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    private final Path rootLocation = Paths.get("uploads");

    public File saveFile(MultipartFile file, String description, boolean isPublic, User owner) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        if (!Files.exists(rootLocation)) {
            Files.createDirectories(rootLocation);
        }

        Files.copy(file.getInputStream(), rootLocation.resolve(uniqueFilename));

        File fileEntity = new File();
        fileEntity.setFileName(originalFilename);
        fileEntity.setFileType(file.getContentType());
        fileEntity.setFilePath(uniqueFilename);
        fileEntity.setFileSize(file.getSize());
        fileEntity.setDescription(description);
        fileEntity.setPublic(isPublic);
        fileEntity.setOwner(owner);

        return fileRepository.save(fileEntity);
    }

    public List<File> getFilesByOwner(User owner) {
        return fileRepository.findByOwner(owner);
    }

    public List<File> getPublicFiles() {
        return fileRepository.findByIsPublicTrue();
    }

    public File getFileWithAccessCheck(Long fileId, User user) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // Vérifier si l'utilisateur est le propriétaire
        if (file.getOwner().getId().equals(user.getId())) {
            return file;
        }

        // Vérifier si le fichier est public
        if (file.isPublic()) {
            return file;
        }

        // Vérifier si l'utilisateur a une demande d'accès approuvée
        boolean hasAccess = fileRepository.checkUserAccess(fileId, user.getId());
        if (hasAccess) {
            return file;
        }

        throw new RuntimeException("Access denied");
    }

    public void deleteFile(Long fileId, User user) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!file.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("You are not the owner of this file");
        }

        try {
            Files.deleteIfExists(rootLocation.resolve(file.getFilePath()));
            fileRepository.delete(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file");
        }
    }
}