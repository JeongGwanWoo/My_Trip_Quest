package com.ssafy.tripquest.controller;

import com.ssafy.tripquest.domain.User;
import com.ssafy.tripquest.mapper.UserMapper;
import com.ssafy.tripquest.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        User user = userMapper.findById(userId);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{userId}/character-customization")
    public ResponseEntity<User> updateCharacter(
            @PathVariable String userId,
            @RequestBody Map<String, Object> payload) {
        log.debug("캐릭터 커스터마이징 호출: {}", payload.get("accessory"));
        String accessoryToToggle = (String) payload.get("accessory");
        if (accessoryToToggle == null || accessoryToToggle.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            User updatedUser = userService.toggleAccessory(userId, accessoryToToggle);
            log.debug("캐릭터 커스터마이징 업데이트 완료: {}", updatedUser);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            // 예: 사용자를 찾을 수 없는 경우
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{userId}/character-image")
    public ResponseEntity<byte[]> getCharacterImage(@PathVariable String userId) {
        try {
            byte[] imageBytes = userService.generateCharacterImage(userId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(imageBytes.length);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            // 사용자를 찾을 수 없는 경우에 해당
            return ResponseEntity.notFound().build();
        }
    }
}
