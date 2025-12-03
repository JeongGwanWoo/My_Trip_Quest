package com.ssafy.tripquest.domain;

import java.sql.Timestamp;

public class User {
    /** 사용자 ID (PK) */
    private Long userId;
    /** 이메일 (로그인용) */
    private String email;
    /** 닉네임 */
    private String nickname;
    /** 보유 포인트 */
    private Integer pointBalance;
    /** 프로필 이미지 URL */
    private String profileImage;
    /** 캐릭터 커스터마이징 상태 (JSON 또는 문자열) */
    private String characterCustomization;
    /** 가입일 */
    private Timestamp createdAt;
    /** 최종 수정일 */
    private Timestamp updatedAt;

    public User() {
    }

    public User(Long userId, String email, String nickname, Integer pointBalance, String profileImage, String characterCustomization, Timestamp createdAt, Timestamp updatedAt) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.pointBalance = pointBalance;
        this.profileImage = profileImage;
        this.characterCustomization = characterCustomization;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getPointBalance() {
        return pointBalance;
    }

    public void setPointBalance(Integer pointBalance) {
        this.pointBalance = pointBalance;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getCharacterCustomization() {
        return characterCustomization;
    }

    public void setCharacterCustomization(String characterCustomization) {
        this.characterCustomization = characterCustomization;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
