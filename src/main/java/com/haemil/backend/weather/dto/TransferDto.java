package com.haemil.backend.weather.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class TransferDto {
  private String x;
  private String y;
}
