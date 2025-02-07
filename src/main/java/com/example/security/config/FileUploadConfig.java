package com.example.security.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import javax.servlet.MultipartConfigElement;
import java.io.File;

@Configuration
public class FileUploadConfig {

    private static final DataSize MAX_FILE_SIZE = DataSize.ofMegabytes(10);
    private static final DataSize MAX_REQUEST_SIZE = DataSize.ofMegabytes(10);
    private static final String UPLOAD_DIRECTORY = "uploads";

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(MAX_FILE_SIZE);
        factory.setMaxRequestSize(MAX_REQUEST_SIZE);

        // 创建上传目录
        File uploadDir = new File(UPLOAD_DIRECTORY);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        return factory.createMultipartConfig();
    }
}