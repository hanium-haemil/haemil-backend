package com.haemil.backend.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SuccessDto {
    private Boolean success;
}
