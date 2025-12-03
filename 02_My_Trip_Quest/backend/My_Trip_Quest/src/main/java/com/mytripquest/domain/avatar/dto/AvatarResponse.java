package com.mytripquest.domain.avatar.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AvatarResponse {
    private List<EquippedItemDto> equippedItems;
}
