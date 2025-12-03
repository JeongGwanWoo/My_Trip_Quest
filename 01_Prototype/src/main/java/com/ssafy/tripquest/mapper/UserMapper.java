package com.ssafy.tripquest.mapper;

import com.ssafy.tripquest.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    void updateUserPoint(@Param("userId") Long userId, @Param("amount") int amount);
    Integer findPointBalanceByUserId(Long userId);
    User findById(@Param("userId") String userId);
    void updateCharacterCustomization(@Param("userId") String userId, @Param("customizationData") String customizationData);
    void updateProfileImage(@Param("userId") String userId, @Param("profileImage") String profileImage);
}
