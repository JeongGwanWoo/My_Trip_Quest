package com.ssafy.tripquest.domain;

import lombok.Data;

@Data
public class Mission {
    /** 미션 ID */
    private Long missionId;
    /** 관광지 번호 (FK) */
    private Integer spotNo;
    /** 미션 순서 */
    private Integer missionOrder;
    /** 선행 미션 ID */
    private Long unlockCondition;
    /** GPS 재확인 필요 여부 */
    private boolean requireGpsVerify;
    /** 미션 제목 */
    private String title;
    /** 미션 설명 */
    private String description;
    /** 미션 타입 (ARRIVAL, PHOTO 등) */
    private String missionType;
    /** 보상 포인트 */
    private Integer rewardPoint;
}
