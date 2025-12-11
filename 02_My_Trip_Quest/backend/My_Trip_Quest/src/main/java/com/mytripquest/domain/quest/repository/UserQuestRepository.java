package com.mytripquest.domain.quest.repository;

import com.mytripquest.domain.quest.dto.InProgressQuestDto;
import com.mytripquest.domain.quest.entity.QuestStatus;
import com.mytripquest.domain.quest.entity.UserQuest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserQuestRepository {

    Optional<UserQuest> findByUserIdAndQuestId(@Param("userId") long userId, @Param("questId") long questId);

    Optional<UserQuest> findCompletedByUserIdAndQuestId(@Param("userId") long userId, @Param("questId") long questId);

    int countIncompleteLocationsByArea(@Param("userId") Long userId, @Param("areaCode") String areaCode);
    
    void save(UserQuest userQuest);
    void update(UserQuest userQuest);
    void delete(UserQuest userQuest);
    List<InProgressQuestDto> findUserQuestsByStatus(@Param("userId") Long userId, @Param("status") QuestStatus status);

    List<UserQuest> findByUserIdAndQuestIds(@Param("userId") long userId, @Param("questIds") List<Long> questIds);

}
