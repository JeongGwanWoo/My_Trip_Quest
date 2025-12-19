package com.mytripquest.domain.activitylog.repository;

import com.mytripquest.domain.activitylog.dto.ActivityLogResponseDto;
import com.mytripquest.domain.activitylog.entity.ActivityLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ActivityLogMapper {
    void save(ActivityLog log);
    List<ActivityLogResponseDto> findByUserId(@Param("userId") Long userId);
}
