package com.mytripquest.domain.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j // 로깅 사용
@RequiredArgsConstructor
public class LocalFileUploadService implements FileUploadService {

	@Value("${file.upload.dir}")
	private String uploadDir;
	
	@Value("${file.upload.url-path}")
	private String uploadUrlPath;

    @Override
    public String upload(MultipartFile file) throws IOException {
        // 1. 디렉토리 생성 (폴더가 없으면 만듭니다.)
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 2. 파일명 생성 (겹치지 않도록 UUID 사용)
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID().toString() + extension;
        
        // 3. 파일 저장 경로 설정
        Path filePath = Paths.get(uploadDir, uniqueFileName);

        try {
            // 4. 파일 저장 실행 (Transfer to local disk)
            file.transferTo(filePath);
            log.info("File uploaded successfully to: {}", filePath);
            
            // 5. 클라이언트가 접근할 수 있는 URL 경로 반환 (ResourceHandler 설정 기반)
            // 예: http://localhost:8080/images/abc.jpg
            return uploadUrlPath + uniqueFileName;
            
        } catch (IOException e) {
            log.error("Failed to upload file: {}", uniqueFileName, e);
            throw new IOException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }
}