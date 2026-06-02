package com.mytripquest.domain.avatar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder // ★ 이게 있어야 .builder()를 쓸 수 있습니다!
@NoArgsConstructor
@AllArgsConstructor
public class EquippedItemDto {
    private Long itemId;      // Service에서 .itemId()로 넣는 필드
    private String name;      // .name()
    private String slot;      // .slot()
    private String imageUrl;  // .imageUrl()
}