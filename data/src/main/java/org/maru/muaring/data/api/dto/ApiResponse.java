package org.maru.muaring.data.api.dto;

public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public T getData() { return data; }

    // 성공 여부 판단
    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }
}
