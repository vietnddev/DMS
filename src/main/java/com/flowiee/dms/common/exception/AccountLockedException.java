package com.flowiee.dms.common.exception;

import com.flowiee.dms.common.utils.constants.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
public class AccountLockedException extends BaseException {
    public AccountLockedException() {
        this(null);
    }

    public AccountLockedException(String message) {
        this(message, null, null, false);
    }

    public AccountLockedException(String message, Class sourceClass, Throwable sourceException, boolean redirectErrorUI) {
        super(ErrorCode.ACCOUNT_LOCKED, new Object[]{}, message, sourceClass, sourceException, redirectErrorUI);
    }
}