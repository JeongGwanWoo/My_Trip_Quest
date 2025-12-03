package com.mytripquest.domain.file.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileUploadService {
    /**
     * 파일을 저장소에 업로드하고, 저장된 파일의 접근 URL을 반환합니다.
     * @param file 클라이언트로부터 받은 파일 객체
     * @return 저장소에 접근 가능한 공개 URL 또는 경로
     * @throws IOException 파일 처리 중 문제 발생 시
     */
    String upload(MultipartFile file) throws IOException;
}