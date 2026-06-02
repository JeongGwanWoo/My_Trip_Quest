package com.mytripquest.domain.pointhistory.repository;

import com.mytripquest.domain.pointhistory.PointHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PointHistoryMapper {
    List<PointHistory> findByUserId(@Param("userId") Long userId);
}
