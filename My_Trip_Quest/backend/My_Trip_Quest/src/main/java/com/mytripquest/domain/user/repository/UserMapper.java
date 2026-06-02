package com.mytripquest.domain.user.repository;

import com.mytripquest.domain.ranking.dto.UserRankDto;
import com.mytripquest.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {

    Optional<User> findById(Long userId);

    void save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    void updateUser(User user);

    List<User> findTopUsersByPoints(int limit);

    Optional<UserRankDto> findUserRankById(long userId);

    Optional<Long> findIdByEmail(String email); // Added method

    void updatePasswordByEmail(@Param("email") String email, @Param("passwordHash") String passwordHash);

    void deleteById(long userId);

    List<User> findAll();

    List<Integer> findAllUserXp(); // 레벨 분포 계산용 모든 유저 XP 조회

}
