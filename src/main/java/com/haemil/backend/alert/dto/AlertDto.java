package com.haemil.backend.alert.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertDto {
    private String msg;
    private String location;
}