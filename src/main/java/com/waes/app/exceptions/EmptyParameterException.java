package com.waes.app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * EmptyParameterException is thrown when String parameters given to REST endpoints are empty or null.
 */
@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Empty parameter")
public class EmptyParameterException extends RuntimeException
{
    private static final long serialVersionUID = 1690609224348816431L;

    public EmptyParameterException(String message)
    {
        super(message);
    }

    public EmptyParameterException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
