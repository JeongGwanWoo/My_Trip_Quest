package com.mytripquest.domain.quest.repository;

import com.mytripquest.domain.quest.entity.Quest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 퀘스트 데이터에 접근하기 위한 MyBatis Mapper 인터페이스
 */
@Mapper
public interface QuestRepository {
    /**
     * 지역 코드를 기준으로 퀘스트 목록을 조회합니다.
     * @param areaCode
     * @return 퀘스트 리스트
     */
    List<Quest> findByAreaCode(@Param("areaCode") String areaCode);
}
