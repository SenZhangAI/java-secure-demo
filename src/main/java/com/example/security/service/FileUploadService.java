package com.example.security.service;

import com.example.security.validator.FileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final String UPLOAD_DIR = "uploads";

    @Autowired
    private FileValidator fileValidator;

    public String uploadFile(MultipartFile file) throws IOException {
        if (!fileValidator.isValidFile(file)) {
            throw new IllegalArgumentException("不支持的文件类型");
        }

        // 创建上传目录
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 生成安全的文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + extension;
        String sanitizedFileName = fileValidator.sanitizeFileName(newFileName);

        // 保存文件
        Path targetLocation = Paths.get(UPLOAD_DIR).resolve(sanitizedFileName);
        Files.copy(file.getInputStream(), targetLocation);

        return sanitizedFileName;
    }
}