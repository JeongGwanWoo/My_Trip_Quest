package com.ssafy.tripquest.mapper;

import com.ssafy.tripquest.domain.TouristSpot;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Optional;

@Mapper
public interface TouristSpotMapper {
    Optional<TouristSpot> findById(Integer spotNo);
    List<TouristSpot> findAll();
}
