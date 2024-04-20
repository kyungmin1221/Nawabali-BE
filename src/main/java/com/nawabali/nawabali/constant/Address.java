package com.nawabali.nawabali.constant;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Objects;

@Embeddable
@Getter
public class Address {

    private String city;    // 도시

    private String district;    // 구

    public Address() {}

    // 필요시 추가 생성자 필요
    public Address(String city, String district) {
        this.city = city;
        this.district = district;

    }


}
