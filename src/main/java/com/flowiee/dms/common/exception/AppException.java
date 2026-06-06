package com.flowiee.dms.common.exception;

import com.flowiee.dms.common.utils.constants.ErrorCode;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AppException extends BaseException {
    @Serial
    private static final long serialVersionUID = 1L;

    public AppException() {
        this("");
    }

    public AppException(String message) {
        this(message, null);
    }

    public AppException(Throwable cause) {
        this("", cause);
    }

    public AppException(String message, Throwable cause) {
        this(ErrorCode.SYSTEM_ERROR, new Object[]{}, message, null, cause);
    }

    public AppException(@NonNull ErrorCode errorCode, Object[] errorMsgParameter, String message, Class sourceClass, Throwable sourceException) {
        this(errorCode, errorMsgParameter, message, sourceClass, sourceException, false);
    }

    public AppException(@NonNull ErrorCode errorCode, Object[] errorMsgParameter, String message, Class sourceClass, Throwable sourceException, boolean redirectErrorUI) {
        super(errorCode, errorMsgParameter, message, sourceClass, sourceException, redirectErrorUI);
    }
}