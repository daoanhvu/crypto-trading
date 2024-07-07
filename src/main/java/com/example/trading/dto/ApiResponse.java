package com.example.trading.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ApiResponse<T> {
    private String message;
    @JsonIgnore
    private int httpStatusCode;
    private T result;

    public ApiResponse() {
        this.httpStatusCode = 200;
    }

    public ApiResponse(String message, int httpStatusCode, T result) {
        this.message = message;
        this.httpStatusCode = httpStatusCode;
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
