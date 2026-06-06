package com.flowiee.dms.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.CONFLICT)
public class DataExistsException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 1L;
	
	public DataExistsException(String message) {
		super(message);
	}
}