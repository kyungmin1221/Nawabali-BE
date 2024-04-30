package com.nawabali.nawabali.constant;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Embeddable
@Getter
public class Town {

    @NotNull(message = "위도는 필수 값입니다.")
    private Double latitude;    // 위도

    @NotNull(message = "경도는 필수 값입니다.")
    private Double longitude;    // 경도

    @NotBlank(message = "구는 필수 값입니다.")
    private String district;    // 구

    private String placeName;

    @NotBlank(message = "장소 주는 필수 값입니다.")
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
