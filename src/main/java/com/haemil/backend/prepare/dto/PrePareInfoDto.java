package com.haemil.backend.prepare.dto;
import lombok.Data;

@Data
public class PrePareInfoDto {
    private String mask; // 미세먼지 농도 값에 따른 마스크 착용 -> 옵션 ) 자율, 권고, 필수
    private String clothes; // 기온별 옷차림 -> 옵션 ) ex. "민소매, 반팔, 반바지, 원피스"
    private boolean umbrella; // 강수량에 따른 우선 여부
    private int percent; // 외출 적합도 퍼센트
    private String result; // 외출 적합도 문장 -> 옵션 ) ex. "외출하기 좋은 날이네요"ㅇ
}
