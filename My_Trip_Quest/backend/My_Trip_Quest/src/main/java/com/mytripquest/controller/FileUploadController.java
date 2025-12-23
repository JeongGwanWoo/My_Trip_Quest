package com.mytripquest.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mytripquest.domain.file.service.FileUploadService;
import com.mytripquest.global.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {
	
	private final FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ApiResponse<String> uploadSingleFile(@RequestParam("file") MultipartFile file
    		, @RequestParam(value = "userId", required = false) Long userId) throws IOException {
    	
        log.debug("file 모듈 호출");
    	Long finalUserId = (userId != null) ? userId : 999L;
    	
        // 1. 서비스 호출 (S3 업로드 로직 실행)
        String uploadedUrl = fileUploadService.upload(file);
        
        // 2. 저장된 URL만 클라이언트에 반환
        return ApiResponse.success(uploadedUrl); 
    }
}
