package com.mytripquest.domain.systemlog.dto;

import lombok.Data;
import java.util.List;

@Data
public class EconomyStatDto {
    private long totalPointsEarned;
    private long totalPointsSpent;
    private List<ItemSalesDto> topSellingItems;

    @Data
    public static class ItemSalesDto {
        private String itemName;
        private long salesCount;
    }
}
