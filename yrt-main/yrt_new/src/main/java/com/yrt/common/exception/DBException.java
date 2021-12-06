package com.yrt.common.exception;

public class DBException extends RuntimeException {

	private static final long serialVersionUID = 1L;

    protected final String message;

    public DBException(String message)
    {
        this.message = message;
    }

    @Override
    public String getMessage()
    {
        return message;
    }
}
