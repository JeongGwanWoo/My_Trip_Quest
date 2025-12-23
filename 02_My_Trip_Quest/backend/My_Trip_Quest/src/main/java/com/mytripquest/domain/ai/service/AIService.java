package com.mytripquest.domain.ai.service;

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
public class AIService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=";

    /**
     * 사진이 해당 관광지(랜드마크)인지 검증
     */
    public boolean isPhotoOfLandmark(byte[] imageBytes, String landmarkName) {
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        String prompt = String.format(
                "Is this a photo of '%s'? Answer only with 'YES' or 'NO'. Do not include any other text.",
                landmarkName);

        Map<String, Object> imagePart = Map.of(
                "inlineData", Map.of(
                        "mimeType", "image/jpeg",
                        "data", base64Image));

        Map<String, Object> textPart = Map.of(
                "text", prompt);

        return callGeminiApi(new Object[] { textPart, imagePart })
                .map(text -> "YES".equals(text.toUpperCase()))
                .orElse(false);
    }

    /**
     * 관광지 이름과 주소를 기반으로 적절한 인증 반경(m) 추정
     */
    public int estimateLocationRadius(String locationName, String address) {
        String prompt = String.format(
                "Estimate the appropriate GPS verification radius (in meters) for a tourist location named '%s' located at '%s'. "
                        +
                        "Return ONLY an integer value. " +
                        "Examples: A large park -> 300, A small statue -> 30, A building -> 50. " +
                        "Do not output unit or any other text.",
                locationName, address);

        Map<String, Object> textPart = Map.of(
                "text", prompt);

        return callGeminiApi(new Object[] { textPart })
                .map(text -> {
                    try {
                        String cleaned = text.replaceAll("[^0-9]", "");
                        return Integer.parseInt(cleaned);
                    } catch (NumberFormatException e) {
                        log.warn("Failed to parse radius from AI response: {}", text);
                        return 50; // 기본값
                    }
                })
                .orElse(50); // 실패 시 기본값
    }

    private java.util.Optional<String> callGeminiApi(Object[] parts) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> partContainer = Map.of("parts", parts);
        Map<String, Object> requestBody = Map.of("contents", new Object[] { partContainer });

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            String url = GEMINI_API_URL + geminiApiKey;
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode textNode = rootNode.path("candidates").path(0).path("content").path("parts").path(0)
                        .path("text");

                if (textNode.isTextual()) {
                    return java.util.Optional.of(textNode.asText().trim());
                }
            }
        } catch (HttpClientErrorException e) {
            log.error("Gemini API HTTP Error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Gemini API Call Error: {}", e.getMessage(), e);
        }
        return java.util.Optional.empty();
    }
}
