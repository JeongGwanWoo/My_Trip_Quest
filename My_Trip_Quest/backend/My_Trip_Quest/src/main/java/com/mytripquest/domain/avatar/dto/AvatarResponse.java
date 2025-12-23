package com.mytripquest.domain.avatar.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AvatarResponse {
    private int points;
    private List<EquippedItemDto> equippedItems;
}
