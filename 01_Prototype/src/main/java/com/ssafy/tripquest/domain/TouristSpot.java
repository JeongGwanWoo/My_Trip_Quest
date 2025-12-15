package com.ssafy.tripquest.domain;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class TouristSpot {
    /** 관광지 번호 (PK) */
    private Integer no;
    /** 관광지명 */
    private String title;
    /** 위도 */
    private BigDecimal latitude;
    /** 경도 */
    private BigDecimal longitude;
    /** GPS 인증 허용 반경 (미터) */
    private Integer gpsVerifyRange;
}
