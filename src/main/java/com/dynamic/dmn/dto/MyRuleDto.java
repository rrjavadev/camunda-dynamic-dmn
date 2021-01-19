package com.dynamic.dmn.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MyRuleDto {

    private String input1;
    private String input2;
    private String input3;
    private String output;
}
