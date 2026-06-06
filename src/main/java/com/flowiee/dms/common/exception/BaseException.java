package com.flowiee.dms.common.exception;

import com.flowiee.dms.common.utils.constants.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BaseException extends RuntimeException {
    private ErrorCode  errorCode;
    private Object[]   errorMsgParameter;
    private String     message;
    private Class      sourceClass;
    private Throwable  sourceException;
    private boolean    isRedirectView;
    private String     view;
    private HttpStatus httpStatus;

    public BaseException(ErrorCode errorCode, Object[] errorMsgParameter, String message, Class sourceClass, Throwable sourceException, boolean isRedirectView) {
        super((message != null && !message.isBlank()) ? message : errorCode.getDescription(), sourceException);
        this.errorCode = errorCode;
        this.errorMsgParameter = errorMsgParameter;
        this.message = message;
        this.sourceClass = sourceClass;
        this.sourceException = sourceException;
        this.isRedirectView = isRedirectView;
        if (errorCode.equals(ErrorCode.ACCOUNT_LOCKED)) {
            this.httpStatus = HttpStatus.LOCKED;
        }
        setMessage(getDisplayMessage());
    }

    public String getDisplayMessage() {
        String lvPrefixCode = "[" + errorCode.getCode() + "] ";
        String lvMessage = (message != null && !message.isBlank()) ? message : errorCode.getDescription();
        if (errorMsgParameter != null && errorMsgParameter.length > 0) {
            for (int i = 0; i < errorMsgParameter.length; i++) {
                if (lvMessage.contains("{" + i + "}")) {
                    lvMessage = lvMessage.replace("{" + i + "}", errorMsgParameter[i].toString());
                }
            }
        }
        return lvPrefixCode + lvMessage;
    }
}