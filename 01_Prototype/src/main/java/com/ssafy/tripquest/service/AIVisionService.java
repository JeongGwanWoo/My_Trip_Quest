package com.ssafy.tripquest.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIVisionService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=";

    public boolean isPhotoOfLandmark(byte[] imageBytes, String landmarkName) {
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String prompt = String.format("이 사진이 대한민국에 있는 '%s'의 사진이 맞습니까? 답변은 오직 'YES' 또는 'NO'로만 해주세요. 다른 텍스트는 포함하지 마세요.", landmarkName);

        Map<String, Object> imagePart = Map.of(
                "inlineData", Map.of(
                        "mimeType", "image/jpeg", // JPEG로 가정, 이상적으로는 MIME 타입 감지 필요
                        "data", base64Image
                )
        );

        Map<String, Object> textPart = Map.of(
                "text", prompt
        );

        Map<String, Object> partContainer = Map.of("parts", new Object[]{textPart, imagePart});
        Map<String, Object> requestBody = Map.of("contents", new Object[]{partContainer});

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            String url = GEMINI_API_URL + geminiApiKey;
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Gemini API 응답: {}", response.getBody());
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode textNode = rootNode.path("candidates").path(0).path("content").path("parts").path(0).path("text");
                
                if (textNode.isTextual()) {
                    String aiResponseText = textNode.asText().trim().toUpperCase();
                    return "YES".equals(aiResponseText);
                }
            }
        } catch (HttpClientErrorException e) {
            log.error("Gemini API HTTP 오류: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Gemini API 호출 중 오류 발생: {}", e.getMessage(), e);
        }

        return false; // 오류 또는 불분명한 응답 시 기본적으로 false 반환
    }
}
