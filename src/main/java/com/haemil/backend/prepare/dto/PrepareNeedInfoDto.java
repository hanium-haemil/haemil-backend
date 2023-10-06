package com.haemil.backend.prepare.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class PrepareNeedInfoDto {
  private String mask; // 미세먼지 농도 값에 따른 마스크 착용 -> 옵션 ) 자율, 권고, 필수
  private String umbrella; // 강수량에 따른 우선 여부
  private String clothes; // 기온별 옷차림 -> 옵션 ) ex. "민소매, 반팔, 반바지, 원피스"
}
