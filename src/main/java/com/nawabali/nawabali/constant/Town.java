package com.nawabali.nawabali.constant;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Town {

    private Double latitude;    // 위도

    private Double longitude;    // 경도

    private String district;    // 구

    private String placeName;

    private String placeAddr;

    protected Town() {}  //무분별한 생성을 막기 위해 protected 로 선언

    // 필요시 추가 생성자 필요
    public Town(Double latitude, Double longitude, String district, String placeName, String placeAddr) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.district = district;
        this.placeName = placeName;
        this.placeAddr = placeAddr;
    }
}
