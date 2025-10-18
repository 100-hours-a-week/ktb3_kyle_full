package com.kyle.week4.swagger;

import io.swagger.v3.oas.models.examples.Example;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExampleHolder {
    private Example holder;
    private String name;
    private String message;
    private Integer code;
}
