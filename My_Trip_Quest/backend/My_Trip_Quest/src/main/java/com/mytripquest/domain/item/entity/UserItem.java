package com.mytripquest.domain.item.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserItem {
    
    // ★ [부활] MyBatis가 "이거 다른 데이터구나"라고 알게 하려면 PK 역할을 할 필드가 필요합니다.
    // DB 테이블에 이 컬럼이 없더라도, 매핑을 위해 추가합니다.
    private Long itemId; 

    private Long userId;
    
    // 상세 정보는 여전히 여기에 담깁니다.
    private Item item; 

    private int quantity;
    private boolean isEquipped;
    private LocalDateTime acquiredAt;
    private LocalDateTime updatedAt;
}