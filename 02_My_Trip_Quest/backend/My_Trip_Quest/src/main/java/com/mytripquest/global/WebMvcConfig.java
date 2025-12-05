package com.mytripquest.global;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	@Value("${file.upload.dir}")
	private String uploadDir;
	
	@Value("${file.upload.url-path}")
	private String uploadUrlPath;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 업로드 파일에 대한 핸들러 설정
        // 요청 경로: /images/** // 실제 위치: file:C:/mytripquest_data/uploads/
        registry.addResourceHandler(uploadUrlPath + "**")
                // file: 접두사는 파일 시스템의 경로임을 Spring에게 알려줍니다.
                .addResourceLocations("file:" + uploadDir);

        // 정적 리소스 핸들러 (img 폴더는 이미 있으니 그대로 유지)
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/img/");
	}
	
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // ★ 중요: allowedOrigins 대신 allowedOriginPatterns 사용 권장
                .allowedOriginPatterns("http://localhost:5173") 
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true) // 이게 true면, 위에서 * 를 쓰면 안 됨!
                .maxAge(3600);
    }

}
