package com.mytripquest.domain.systemlog.mapper;

import com.mytripquest.domain.systemlog.dto.SystemLogDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface SystemLogMapper {
    void insertLog(SystemLogDto logDto);

    // 통계 쿼리
    List<Map<String, Object>> selectErrorRateStats(); // 최근 1시간 에러율 (10분 단위)

    // Economy Stats
    Long selectTotalPointsEarned(); // 총 획득 포인트 (Quest Reward)

    Long selectTotalPointsSpent(); // 총 소비 포인트 (Shop Purchase)

    java.util.List<com.mytripquest.domain.systemlog.dto.EconomyStatDto.ItemSalesDto> selectTopSellingItems(); // 인기 아이템
                                                                                                              // Top 5

    List<Map<String, Object>> selectSlowestFeatures(); // 평균 실행 시간 Top 5

    List<Map<String, Object>> selectTrafficStats(); // 최근 24시간 트래픽
}
