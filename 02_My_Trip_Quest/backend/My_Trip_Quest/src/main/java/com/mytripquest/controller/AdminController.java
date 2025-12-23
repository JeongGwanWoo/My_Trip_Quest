package com.mytripquest.controller;

import com.mytripquest.domain.quest.dto.QuestStatDto;
import com.mytripquest.domain.user.dto.AdminUserResponseDto;
import com.mytripquest.domain.user.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/hello")
    public ResponseEntity<String> helloAdmin() {
        return ResponseEntity.ok("Hello, Admin!");
    }

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<Void> updateUserRole(@PathVariable Long userId, @RequestBody com.mytripquest.domain.user.dto.UpdateUserRoleRequestDto request) {
        adminService.updateUserRole(userId, request.getRole());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats/quests")
    public ResponseEntity<List<QuestStatDto>> getQuestStats() {
        return ResponseEntity.ok(adminService.getQuestCompletionStats());
    }
}
