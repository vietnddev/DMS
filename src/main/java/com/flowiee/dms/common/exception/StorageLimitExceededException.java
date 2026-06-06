package com.flowiee.dms.common.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
@Data
public class StorageLimitExceededException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public StorageLimitExceededException(String message) {
        this(message, null);
    }

    public StorageLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}