package com.mytripquest.domain.quest.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 지역별 퀘스트 개수 조회를 위한 데이터 전송 객체(DTO)
 */
@Data
@Builder
public class AreaQuestCountDto {
    /** 지역 이름 (영문) */
    private String areaName;
    /** 해당 지역의 퀘스트 개수 */
    private int questCount;
}
