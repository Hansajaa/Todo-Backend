package com.todoapp.todoapi.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseModel {
    private int statusCode;
    private String message;
    private Object body;
}
