package com.nawabali.nawabali.constant;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Town {

    private Double latitude;    // 도시

    private Double longitude;    // 구
    protected Town() {}  //무분별한 생성을 막기 위해 protected 로 선언

    // 필요시 추가 생성자 필요
    public Town(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
