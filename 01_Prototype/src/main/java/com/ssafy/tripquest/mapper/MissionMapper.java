package com.ssafy.tripquest.mapper;

import com.ssafy.tripquest.domain.Mission;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Optional;

@Mapper
public interface MissionMapper {
    Optional<Mission> findBySpotNoAndMissionType(Integer spotNo, String missionType);
    Optional<Mission> findById(Long missionId);
    List<Mission> findBySpotNo(Integer spotNo);
}
