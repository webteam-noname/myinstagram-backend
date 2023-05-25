package com.my.instagram.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class ApiResponse<T> {
    private String result;
    private int statusCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    private T data;

    public ApiResponse(HttpStatus statusCode, T data) {
        this.result = getResultByStatusCode(Integer.toString(statusCode.value()));
        this.statusCode = statusCode.value();
        this.data =data;
    }

    public ApiResponse(HttpStatus statusCode, String message) {
        this.result = getResultByStatusCode(Integer.toString(statusCode.value()));
        this.statusCode = statusCode.value();
        this.message = message;
    }

    public ApiResponse(HttpStatus statusCode, String errorCode,String message) {
        this.result = getResultByStatusCode(Integer.toString(statusCode.value()));
        this.statusCode = statusCode.value();
        this.errorCode = errorCode;
        this.message = message;
    }

    public ApiResponse(int statusCode, String message) {
        this.result = getResultByStatusCode(Integer.toString(statusCode));
        this.statusCode = statusCode;
        this.message = message;
    }

    private String getResultByStatusCode(String statusCode){
        if(statusCode.startsWith("2")){
            return "success";
        }else{
            return "fail";
        }
    }
}
