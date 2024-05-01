package com.nawabali.nawabali.constant;

import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
@Getter
public enum CityEnum {
    SEOUL("서울특별시", Arrays.asList(
            "강남구", "강동구", "강서구", "강북구", "관악구",
            "광진구", "구로구", "금천구", "노원구", "동대문구",
            "도봉구", "동작구", "마포구", "서대문구", "성동구",
            "성북구", "서초구", "송파구", "영등포구", "용산구",
            "양천구", "은평구", "종로구", "중구", "중랑구"));
    private final String city;
    private final List<String> districts;
    CityEnum(String city, List<String> districts){
        this.city = city;
        this.districts = districts;
    }

    public static boolean checkCorrectAddress(String cityName, String districtName) {
        System.out.println("입력한 도시명 : " + cityName);
        System.out.println("입력한 구 명 : " + districtName);
        // 서비스 하고 있는 도시 확인
        for (CityEnum cityEnum : values()) {
            // 서비스 하고 있는 구 확인
            if(cityName.equals(cityEnum.getCity())){
                List<String> cityInDistricts = cityEnum.getDistricts();
                System.out.println("입력한 도시의 구역들 :" + cityInDistricts);

                return cityInDistricts.contains(districtName);
            }
        }
        throw new CustomException(ErrorCode.INVALID_CITY_NAME);
    }
}
