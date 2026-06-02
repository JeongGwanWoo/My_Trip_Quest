package com.mytripquest.domain.quest.repository;

import com.mytripquest.domain.quest.dto.LocationWithQuestCountDto;
import com.mytripquest.domain.quest.entity.Quest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 퀘스트 데이터에 접근하기 위한 MyBatis Mapper 인터페이스
 */
@Mapper
public interface QuestRepository {
        /**
         * 지역 코드를 기준으로, 퀘스트가 있는 관광지 목록과 각 관광지의 퀘스트 개수를 조회합니다.
         * 
         * @param areaCode
         * @return 관광지 정보와 퀘스트 개수를 담은 DTO 리스트
         */
        List<LocationWithQuestCountDto> findLocationsByAreaCode(@Param("areaCode") String areaCode,
                        @Param("keyword") String keyword, @Param("pageable") Pageable pageable);

        /**
         * 퀘스트가 있는 모든 관광지 목록을 조회합니다.
         * 
         * @return 관광지 정보와 퀘스트 개수를 담은 DTO 리스트
         */
        List<LocationWithQuestCountDto> findAllLocations();

        /**
         * 퀘스트가 존재하는 모든 지역 코드를 중복 없이 조회합니다.
         * 
         * @return 지역 코드 리스트
         */
        List<String> findDistinctAreaCodes();

        /**
         * 지역 코드를 기준으로, 해당 지역에 속한 모든 퀘스트 목록을 조회합니다.
         * 
         * @param areaCode
         * @return 퀘스트 리스트
         */
        List<Quest> findQuestsByAreaCode(@Param("areaCode") String areaCode);

        /**
         * 특정 관광지 ID를 기준으로 해당 관광지에 속한 퀘스트 목록을 조회합니다.
         * 
         * @param locationId
         * @return 퀘스트 리스트
         */
        List<Quest> findQuestsByLocationId(@Param("locationId") Long locationId);

        /**
         * 퀘스트 ID로 퀘스트 단건을 조회합니다.
         * 
         * @param questId
         * @return 퀘스트 Optional
         */
        Optional<Quest> findQuestById(@Param("questId") long questId);

        /**
         * 특정 지역의 총 관광지 개수를 조회합니다.
         * 
         * @param areaCode
         * @return 총 관광지 개수
         */
        int countTotalLocationsByArea(@Param("areaCode") String areaCode);

        /**
         * 모든 퀘스트의 총 개수를 조회합니다.
         * 
         * @return 퀘스트 총 개수
         */
        long countAll();

        /**
         * 특정 지역의 총 퀘스트 개수를 조회합니다.
         * 
         * @param areaCode
         * @return 특정 지역의 퀘스트 총 개수
         */
        int countQuestsByArea(@Param("areaCode") String areaCode);

        Optional<LocationWithQuestCountDto> findLocationById(@Param("locationId") Long locationId);

        Optional<Quest> findFirstByLocationIdAndQuestTypeIdOrderByQuestIdAsc(@Param("locationId") long locationId,
                        @Param("questTypeId") int questTypeId);

        Long findMaxLocationIdByRange(@Param("startId") Long startId, @Param("endId") Long endId);

        Long findMaxQuestIdByRange(@Param("startId") Long startId, @Param("endId") Long endId);

        void saveLocation(LocationWithQuestCountDto location);

        void saveQuest(Quest quest);

        void updateLocationRadius(@Param("locationId") Long locationId, @Param("radius") int radius);

        void updateLocation(@Param("locationId") Long locationId, @Param("latitude") Double latitude,
                        @Param("longitude") Double longitude, @Param("gpsVerifyRadius") Integer gpsVerifyRadius);

        void deleteQuest(@Param("questId") Long questId);
}
