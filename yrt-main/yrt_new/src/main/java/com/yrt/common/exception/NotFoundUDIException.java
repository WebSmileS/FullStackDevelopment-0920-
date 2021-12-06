package com.yrt.common.exception;

public class NotFoundUDIException extends RuntimeException {
	private static final long serialVersionUID = 1L;

    protected final String message;

    public NotFoundUDIException(String message)
    {
        this.message = message;
    }

    @Override
    public String getMessage()
    {
        return message;
    }
}