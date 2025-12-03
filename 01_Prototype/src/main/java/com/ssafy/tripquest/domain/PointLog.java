package com.ssafy.tripquest.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PointLog {
    /** 로그 ID */
    private Long logId;
    /** 사용자 ID (FK) */
    private Long userId;
    /** 거래 유형 (획득, 사용) */
    private TransactionType transactionType;
    /** 포인트 수량 */
    private Integer amount;
    /** 거래 후 잔액 */
    private Integer balanceAfter;
    /** 포인트 출처 타입 (미션, 아이템 구매 등) */
    private SourceType sourceType;
    /** 출처 참조 ID (미션 ID, 아이템 ID 등) */
    private Long sourceId;
    /** 설명 */
    private String description;
    /** 거래 시각 */
    private LocalDateTime createdAt;

    public enum TransactionType {
        EARN, SPEND
    }

    public enum SourceType {
        MISSION, ITEM_PURCHASE, ADMIN
    }
}
