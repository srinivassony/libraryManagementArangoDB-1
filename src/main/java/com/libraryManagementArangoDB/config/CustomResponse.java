package com.libraryManagementArangoDB.config;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CustomResponse<T> {

    private Map<String, Object> responseBody;
    private String statusCodeValue;
    private int statusCode;
    private String path;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timeStamp;

    public CustomResponse(T body, String statusCodeValue, int statusCode, String path, LocalDateTime timeStamp) {
        this.responseBody = new HashMap<>();
        this.statusCodeValue = statusCodeValue;
        this.statusCode = statusCode;
        this.path = path;
        this.timeStamp = timeStamp;

        if (body instanceof String) {
            responseBody.put("message", body);
        } else {
            responseBody.put("data", body);
        }
    }

    public Map<String, Object> getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(T body) {
        if (body instanceof String) {
            this.responseBody.put("message", body);
        } else {
            this.responseBody.put("data", body);
        }
    }

    public String getStatusCodeValue() {
        return statusCodeValue;
    }

    public void setStatusCodeValue(String statusCodeValue) {
        this.statusCodeValue = statusCodeValue;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setResponseBody(Map<String, Object> responseBody) {
        this.responseBody = responseBody;
    }

}
