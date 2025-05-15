package com.example.userservice.service;

import com.example.userservice.config.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileStorageService {

    private final Path storageLocation;

    // Create directory
    @Autowired
    public FileStorageService(FileStorageProperties props) {
        this.storageLocation = Paths.get(props.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(storageLocation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Store avatar and return file name
    public String store(MultipartFile file) {
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if(filename.contains("..")) {
            throw new RuntimeException("Tên file không hợp lệ: " + filename);
        }

        try {
            Path target = storageLocation.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Load image from client
    public Resource loadAsResource(String filename) {
        try {
            Path file = storageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists()) return resource;
            else throw new RuntimeException("File does not exist: " + filename);
        } catch (MalformedURLException e) {
            throw new RuntimeException("File does not exist: " + filename, e);
        }
    }
}
