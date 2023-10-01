package com.haemil.backend.prepare.dto;
import lombok.Data;

@Data
public class PrePareInfoDto {
    private String pm10value; // 가장 최근꺼로 오늘꺼 미세먼지 수치
    private String clothes; // 기온별 옷차림 -> 옵션 ) ex. "민소매, 반팔, 반바지, 원피스"
    private String feel_like; // 체감 온도
    private String uv; // 자외선
    private String result; // 외출 적합도 문장 -> 옵션 ) ex. "외출하기 좋은 날이네요"
    private int percent; // 외출 적합도 퍼센트
}
