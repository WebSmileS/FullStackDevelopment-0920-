package com.yrt.common.exception;

public class DuplicateException extends RuntimeException {

	private static final long serialVersionUID = 1L;

    protected final String message;

    public DuplicateException(String message)
    {
        this.message = message;
    }

    @Override
    public String getMessage()
    {
        return message;
    }
}
