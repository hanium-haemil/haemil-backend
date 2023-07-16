package com.haemil.backend.alert.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component // component 말고 repository로 빼야 하지 않을까
public class GetApiDto {

    private final String apiUrl = "http://apis.data.go.kr/1741000/DisasterMsg3/getDisasterMsg1List";
    private final String type = "JSON";    //데이터 타입

    private String pageNo = "1";    //페이지 번호
    private String numOfRows = "1";    //한 페이지 결과 수
}