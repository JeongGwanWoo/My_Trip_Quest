package com.mytripquest.domain.quest.dto;

import lombok.Data;
import java.util.List;

@Data
public class LocationCreateRequest {
    private String title;
    private String areaCode;
    private String address;
    private Double latitude;
    private Double longitude;
    private Integer gpsVerifyRadius;
    private List<String> questTypes; // ["ARRIVAL", "PHOTO"]
}
