package com.flowiee.dms.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

import java.io.Serializable;

@JsonPropertyOrder({"success", "status", "message", "cause", "pagination", "data"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> implements Serializable {
    @JsonProperty("success")
    Boolean success;

    @JsonProperty("status")
    HttpStatus status;

    @JsonProperty("message")
    String message;

    @JsonProperty("cause")
    String cause;

    @JsonProperty("data")
    T data;

    @JsonProperty("pagination")
    PaginationModel pagination;

    public ApiResponse(Boolean success, HttpStatus status, String message, String cause, T data, PaginationModel pagination) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.cause = cause;
        this.data = data;
        this.pagination = pagination;
    }

    public static <T> ApiResponse<T> ok(@NonNull T data) {
        return ok("OK", data);
    }

    public static <T> ApiResponse<T> ok(@NonNull T data, int pageNum, int pageSize, int totalPage, long totalElements) {
        return ok("OK", data, new PaginationModel(pageNum, pageSize, totalPage, totalElements));
    }

    public static <T> ApiResponse<T> ok(@NonNull String message, @NonNull T data) {
        return ok(message, data, HttpStatus.OK);
    }

    public static <T> ApiResponse<T> ok(@NonNull String message, @NonNull T data, PaginationModel pagination) {
        return ok(message, data, HttpStatus.OK, pagination);
    }

    public static <T> ApiResponse<T> ok(@NonNull String message, @NonNull T data, HttpStatus httpStatus) {
        return new ApiResponse<>(true, httpStatus, message, null, data, null);
    }

    public static <T> ApiResponse<T> ok(@NonNull String message, @NonNull T data, HttpStatus httpStatus, PaginationModel pagination) {
        return new ApiResponse<>(true, httpStatus, message, null, data, pagination);
    }

    public static <T> ApiResponse<T> fail(@NonNull String message, @NonNull Throwable cause, @NonNull HttpStatus httpStatus) {
        return new ApiResponse<>(false, httpStatus, message, cause.getMessage(), null, null);
    }
}