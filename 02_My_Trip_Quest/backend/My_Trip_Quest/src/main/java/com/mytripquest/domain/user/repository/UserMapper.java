package com.mytripquest.domain.user.repository;

import com.mytripquest.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import java.util.Optional;

@Mapper
public interface UserMapper {

    Optional<User> findById(Long userId);

    void save(User user);

    Optional<User> findByEmail(String email);

    void updateUser(User user);

}
