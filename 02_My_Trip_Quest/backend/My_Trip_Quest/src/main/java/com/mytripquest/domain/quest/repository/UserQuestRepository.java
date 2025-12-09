package com.mytripquest.domain.quest.repository;

import com.mytripquest.domain.quest.entity.UserQuest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UserQuestRepository {

    Optional<UserQuest> findByUserIdAndQuestId(@Param("userId") long userId, @Param("questId") long questId);

    Optional<UserQuest> findCompletedByUserIdAndQuestId(@Param("userId") long userId, @Param("questId") long questId);

    int countIncompleteLocationsByArea(@Param("userId") Long userId, @Param("areaCode") String areaCode);
    
    void save(UserQuest userQuest);

}
