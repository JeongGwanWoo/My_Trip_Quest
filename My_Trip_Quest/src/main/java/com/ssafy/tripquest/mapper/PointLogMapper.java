package com.ssafy.tripquest.mapper;

import com.ssafy.tripquest.domain.PointLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.Optional;

@Mapper
public interface PointLogMapper {
    void save(PointLog pointLog);
    Optional<Date> findMissionCompletionTime(@Param("userId") Long userId, @Param("missionId") Long missionId);
}
