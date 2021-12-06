package com.yrt.common.exception;

public class ValidateException extends RuntimeException {
	private static final long serialVersionUID = 1L;

    protected final String message;

    public ValidateException(String message)
    {
        this.message = message;
    }

    @Override
    public String getMessage()
    {
        return message;
    }
}