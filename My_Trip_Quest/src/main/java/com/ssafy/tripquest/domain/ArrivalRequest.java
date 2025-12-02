package com.ssafy.tripquest.domain;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArrivalRequest {
    /** 사용자 위도 */
    private BigDecimal latitude;
    /** 사용자 경도 */
    private BigDecimal longitude;
}
