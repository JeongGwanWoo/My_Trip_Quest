package com.mytripquest.domain.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytripquest.domain.ai.dto.LocationRadiusRequest;
import com.mytripquest.domain.ai.dto.RadiusEstimateResult;
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

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gms.api.key}")
    private String gmsApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=";
    private static final String GMS_API_URL = "https://gms.ssafy.io/gmsapi/generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

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
                        return 150; // 기본값 변경
                    }
                })
                .orElse(150); // 실패 시 기본값 변경
    }

    /**
     * 여러 관광지의 인증 반경을 한 번에 추정 (배치 처리, 토큰 절약)
     */
    public List<RadiusEstimateResult> estimateLocationRadiusBatch(List<LocationRadiusRequest> locations) {
        if (locations == null || locations.isEmpty()) {
            return Collections.emptyList();
        }

        // 토큰 최적화된 배치 프롬프트 생성 (영문)
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Estimate GPS verification radius (meters) for each location. ");
        promptBuilder.append("Return ONLY valid JSON array.\n");
        promptBuilder.append("Format: [{\"name\":\"Name1\",\"radius\":100},{\"name\":\"Name2\",\"radius\":250}]\n");
        promptBuilder.append("Guidelines: Large park→300, Building→50, Temple→100, Plaza→200, Small monument→30.\n\n");
        promptBuilder.append("Locations:\n");

        for (int i = 0; i < locations.size(); i++) {
            LocationRadiusRequest loc = locations.get(i);
            promptBuilder.append(String.format("%d. %s, %s\n",
                    i + 1,
                    loc.getName(),
                    loc.getAddress() != null && !loc.getAddress().isEmpty() ? loc.getAddress() : "South Korea"));
        }

        Map<String, Object> textPart = Map.of("text", promptBuilder.toString());

        Optional<String> responseOpt = callGeminiApi(new Object[] { textPart });

        if (responseOpt.isEmpty()) {
            log.warn("Batch AI radius estimation failed, using defaults");
            return locations.stream()
                    .map(loc -> new RadiusEstimateResult(loc.getName(), 150))
                    .toList();
        }

        String response = responseOpt.get();
        log.info("Batch AI Response: {}", response);

        try {
            // 마크다운 코드블록 제거 (Gemini가 ```json ... ``` 로 감싸서 응답할 수 있음)
            String cleanedResponse = response.trim();
            if (cleanedResponse.startsWith("```json")) {
                cleanedResponse = cleanedResponse.substring(7); // "```json" 제거
            } else if (cleanedResponse.startsWith("```")) {
                cleanedResponse = cleanedResponse.substring(3); // "```" 제거
            }
            if (cleanedResponse.endsWith("```")) {
                cleanedResponse = cleanedResponse.substring(0, cleanedResponse.length() - 3);
            }
            cleanedResponse = cleanedResponse.trim();

            log.info("Cleaned JSON for parsing: {}", cleanedResponse);

            // JSON 응답 파싱
            List<RadiusEstimateResult> results = objectMapper.readValue(
                    cleanedResponse,
                    new TypeReference<List<RadiusEstimateResult>>() {
                    });

            // 결과 검증 및 보정
            if (results.size() != locations.size()) {
                log.warn("AI returned {} results for {} locations, filling missing with defaults",
                        results.size(), locations.size());

                // 부족한 항목은 기본값으로 채움
                while (results.size() < locations.size()) {
                    results.add(new RadiusEstimateResult(
                            locations.get(results.size()).getName(),
                            150));
                }
            }

            // 반경 값 검증 (0 또는 null이면 기본값)
            for (RadiusEstimateResult result : results) {
                if (result.getRadius() == null || result.getRadius() <= 0) {
                    result.setRadius(150);
                }
            }

            return results;

        } catch (Exception e) {
            log.error("Failed to parse batch AI response as JSON: {}", response, e);
            // 파싱 실패 시 모두 기본값 반환
            return locations.stream()
                    .map(loc -> new RadiusEstimateResult(loc.getName(), 150))
                    .toList();
        }
    }

    private java.util.Optional<String> callGeminiApi(Object[] parts) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> partContainer = Map.of("parts", parts);

        // Temperature=0 설정으로 일관된 결과 보장
        Map<String, Object> generationConfig = Map.of("temperature", 0.0);

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] { partContainer },
                "generationConfig", generationConfig);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        log.info("Gemini API Request - temperature: {}", generationConfig.get("temperature"));

        // String url = GEMINI_API_URL + geminiApiKey;
        String url = GMS_API_URL + gmsApiKey;
        // log.debug("Calling Gemini API: {}", url.replaceAll("key=.*", "key=HIDDEN"));

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // 디버깅: 전체 응답 확인
                log.info("=== Gemini API Full Response ===");
                log.info(response.getBody());
                log.info("================================");

                JsonNode rootNode = objectMapper.readTree(response.getBody());

                // 토큰 사용량 로깅 (포트폴리오 근거 자료)
                JsonNode usageMetadata = rootNode.path("usageMetadata");
                if (!usageMetadata.isMissingNode()) {
                    int promptTokens = usageMetadata.path("promptTokenCount").asInt(0);
                    int candidatesTokens = usageMetadata.path("candidatesTokenCount").asInt(0);
                    int totalTokens = usageMetadata.path("totalTokenCount").asInt(0);
                    log.info("📊 Gemini Token Usage - Prompt: {}, Response: {}, Total: {}",
                            promptTokens, candidatesTokens, totalTokens);
                } else {
                    log.warn("⚠️ usageMetadata not found in API response");
                }

                JsonNode textNode = rootNode.path("candidates").path(0).path("content").path("parts").path(0)
                        .path("text");

                if (textNode.isTextual()) {
                    return java.util.Optional.of(textNode.asText().trim());
                }
            } else {
                log.error("Gemini API returned non-success status: {}", response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            log.error("Gemini API HTTP Error: {} - Response: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Gemini API Call Error: {}", e.getMessage(), e);
        }
        return java.util.Optional.empty();
    }
}
